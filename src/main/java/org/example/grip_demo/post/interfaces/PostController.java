package org.example.grip_demo.post.interfaces;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grip_demo.climbinggym.application.ClimingGymService;
import org.example.grip_demo.climbinggym.domain.ClimbingGym;
import org.example.grip_demo.climbinggym.interfaces.ClimbingGymDto;
import org.example.grip_demo.comment.domain.Comment;
import org.example.grip_demo.demo.JwtTokenizer;
import org.example.grip_demo.post.application.PostService;
import org.example.grip_demo.post.domain.Post;
import org.example.grip_demo.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final JwtTokenizer jwtTokenizer;

    private final PostService postService;

    private final ClimingGymService climingGymService;

    @Value("${count.gym.id}")
    private Long gymId;

    @GetMapping("/postlist")
    public String getPostList(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Post> paging = postService.getListPost(page);
        model.addAttribute("paging", paging);
        return "posts/postList";
    }

    @GetMapping("/postform")
    public String getPostForm(@RequestParam(value="climbingid",required = false) Long climbingGymId, Model model,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes){

        try {
            if(climbingGymId == null){
                climbingGymId=gymId;
            }

            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if (token == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "인증되지 않은 사용자입니다.");
                return "redirect:/loginform";
            }


            Claims claims=jwtTokenizer.parseAccessToken(token);

            String name = (String)claims.get("name");

            String userId = claims.get("userId").toString();
            Long longuserId=Long.parseLong(userId);

            model.addAttribute("climbingGymId", climbingGymId);
            model.addAttribute("userId", longuserId);
            model.addAttribute("name", name);
            return "posts/postform";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            return "redirect:/main";
        }
    }

    @PostMapping("/post")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("climbingid") Long climbingGymId,
                             @RequestParam("userId") Long userid,
                             @RequestParam("image") MultipartFile image,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes){

        try {
            PostDto postDto = new PostDto();
            postDto.setTitle(title);
            postDto.setContent(content);
            if (!image.isEmpty()) {
                String imageUrl = saveImage(image);
                postDto.setImageUrl(imageUrl);
            }

            ClimbingGym climbingGym=new ClimbingGym();
            climbingGym.setId(climbingGymId);
            postDto.setClimbingGym(climbingGym);
            User user = new User();
            user.setId(userid);
            postDto.setUser(user);
            Post createdPost = postService.createPost(mapToEntity(postDto));
            return "redirect:/post/" + climbingGymId+"/"+createdPost.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "입력 값 오류가 발생했습니다.");
            return "redirect:/main";
        }
    }

    @GetMapping("/updatepostform")
    public String getUpdatePostForm(@RequestParam("postid") Long postId, Model model,
                                    RedirectAttributes redirectAttributes){
        Optional<Post> postOptional = postService.getPostById(postId);

        if(postOptional.isPresent()){
            model.addAttribute("post", mapToDto(postOptional.get()));
            String name =postOptional.get().getUser().getName();
            model.addAttribute("postId",postId);
            model.addAttribute("name", name);
            model.addAttribute("imageUrl",postOptional.get().getImageUrl());
            return "posts/updatepostform";
        }else {
            redirectAttributes.addFlashAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
            return "redirect:/main";
        }
    }
    // 게시글 수정 과정
    @PostMapping("/updatepost/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<Post> postOptional = postService.getPostById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                post.setTitle(title);
                post.setContent(content);

                if (image != null && !image.isEmpty()) {
                    String imageUrl = saveImage(image);
                    post.setImageUrl(imageUrl);
                }

                postService.updatePost(post);
                return "redirect:/post/"+ post.getClimbingGym().getId()+"/"+ postId;
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
                return "redirect:/main";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "입력 값 오류가 발생했습니다.");
            return "redirect:/main";
        }
    }


    @GetMapping("/deletepost/{postId}")
    public String deletePost(@PathVariable("postId") Long postId, RedirectAttributes redirectAttributes) {
        postService.deletePost(postId);
        return "redirect:/postlist";
    }

    @GetMapping("/post/{climbingid}/{postid}")
    public String postDetail(@PathVariable Long postid,
                             @PathVariable Long climbingid,
                             HttpServletRequest request,
                             Model model,RedirectAttributes redirectAttributes){
        Post post= postService.getPostById(postid).get();
        String name= post.getUser().getName();
        ClimbingGym climbingGym = climingGymService.getClimbingGym(climbingid).get();
        String gymName = climbingGym.getName();
        Long postUserId = post.getUser().getId();
        String stringPostUserId = postUserId.toString();

        //현재 사용중인 사용자 뽑아보리깅
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "인증되지 않은 사용자입니다.");
            return "redirect:/loginform";
        }
        Claims claims=jwtTokenizer.parseAccessToken(token);
        String currentUserId=claims.get("userId").toString();

        model.addAttribute("post", post);
        model.addAttribute("name",name);
        model.addAttribute("gymName",gymName);
        model.addAttribute("currentUserId",currentUserId);
        model.addAttribute("postUserId",postUserId);
        model.addAttribute("stringPostUserId",stringPostUserId);

        return "posts/detail";
    }


    private Post mapToEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setUser_id(postDto.getUser());
        post.setClimbingGymId(postDto.getClimbingGym());
        post.setCreatedAt(LocalDateTime.now());
        post.setImageUrl(postDto.getImageUrl());
        post.setViewCount(0);
        post.setLikeCount(0);
        return post;
    }

    private PostDto mapToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setViewCount(post.getViewCount());
        postDto.setLikeCount(post.getLikeCount());
        return postDto;
    }

    private String saveImage(MultipartFile image) throws Exception {
        String folder = "uploads/";
        Path uploadPath = Paths.get(folder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        byte[] bytes = image.getBytes();
        String imageName = LocalDate.now().toString() + "_" + LocalTime.now().getHour()+"_"+LocalTime.now().getMinute()+"_"+ image.getOriginalFilename();
        Path path = Paths.get(folder + imageName);
        Files.write(path, bytes);
        return "/uploads/" + imageName;
    }


}

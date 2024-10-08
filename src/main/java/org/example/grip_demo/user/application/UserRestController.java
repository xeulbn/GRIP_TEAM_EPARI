package org.example.grip_demo.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.grip_demo.demo.JwtTokenizer;
import org.example.grip_demo.demo.Oauth2Util;
import org.example.grip_demo.user.domain.RedisRefreshToken;
import org.example.grip_demo.user.domain.Role;
import org.example.grip_demo.user.domain.User;
import org.example.grip_demo.user.infrastructure.RedisRefreshTokenRepository;
import org.example.grip_demo.user.interfaces.EmailService;
import org.example.grip_demo.user.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final Oauth2Util oauth2Util;
    private final JwtTokenizer jwtTokenizer;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final EmailService emailService;

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
                                      HttpServletResponse response) throws IOException {
        String token = oauth2Util.getKakaoAcessToken(code);
        String info = oauth2Util.getKakaoInfo(token);// id, nickname 넘어옴

        // id와 nickname 추출
        JsonObject jsonObject = JsonParser.parseString(info).getAsJsonObject();
        String username = jsonObject.get("id").getAsString();
        String name = jsonObject.getAsJsonObject("properties").get("nickname").getAsString();
        // 결과 출력 test

        User user = userService.findUserByUsername("kakao"+username).orElse(null);

        if(user != null){
            //회원 정보가 있을 경우 토큰 전달

            List<String> roles = user.getRoles().stream().map(Role::getName).toList();

            String accessToken = jwtTokenizer.createAccessToken(
                    user.getId(), user.getEmail(), user.getNickName(), user.getName(), user.getUsername(), roles);
            String refreshToken = jwtTokenizer.createRefreshToken(
                    user.getId(), user.getEmail(), user.getNickName(), user.getName(), user.getUsername(), roles);

            //쿠키 생성 후 쿠키 전달
            Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            redisRefreshTokenRepository.save(new RedisRefreshToken(user.getId(), refreshToken));

            response.sendRedirect("/main");
            return null;
        } else {
            //회원 정보가 없을 경우 회원가입 화면으로
            String encodedName = URLEncoder.encode(name, "UTF-8");
            response.sendRedirect("/registeruserform/kakao?username="+username+"&name="+encodedName);
            return null;
        }

    }

    @GetMapping("/api/check/username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = !userService.isUsernameExist(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", isAvailable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/check/email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = !userService.isUserEmailExist(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", isAvailable);
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/check/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickNameAvailability(@RequestParam String nickName) {
        boolean isAvailable = !userService.isUserNickNameExist(nickName);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", isAvailable);
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/sendVerificationCode")
    public ResponseEntity<Map<String, Boolean>> sendVerificationCode(@RequestParam String email) {
        boolean sent = emailService.sendVerificationCode(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("sent", sent);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/verifyCode")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyCode(email, code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isVerified", isVerified);
        return ResponseEntity.ok(response);
    }
}

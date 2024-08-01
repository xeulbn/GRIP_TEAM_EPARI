package org.example.grip_demo.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.grip_demo.climbinggym.domain.ClimbingGym;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Title", length = 255, nullable = true)
    private String title;

    @Column(name = "Content", length = 500, nullable = true)
    private String content;

    @Column(name = "Created_At", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "Updated_At", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "View_Count", nullable = true)
    private int View_Count;

    @Column(name = "Like_Count", nullable = true)
    private int Like_Count;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "CLIMBING_GYM_ID", nullable = true)
    private ClimbingGym climbingGym;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setViewCount(int view_Count) {
        this.View_Count = view_Count;
    }

    public void setLikeCount(int like_Count) {
        this.Like_Count = like_Count;
    }

}


package org.example.grip_demo.post.infrastructure;

import org.example.grip_demo.post.domain.Post;
import org.example.grip_demo.post.domain.PostRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryImpl extends JpaRepository<Post, Long>, PostRepository {

}

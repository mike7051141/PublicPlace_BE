package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.LikePost;
import com.springboot.publicplace.entity.Post;
import com.springboot.publicplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    LikePost findByUserAndPost(User user, Post post);
}

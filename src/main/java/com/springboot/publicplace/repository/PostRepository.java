package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(String category, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleContainingAndCategory(String title, String category, Pageable pageable);

    Page<Post> findByTitleContaining(String title, Pageable pageable);
}

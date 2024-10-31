package com.springboot.publicplace.repository.queryDSL;

import com.springboot.publicplace.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findByTitleContainingAndCategory(String title, String category, Pageable pageable);
    Page<Post> findByTitleContaining(String title, Pageable pageable);
}
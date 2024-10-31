package com.springboot.publicplace.repository.queryDSL.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.publicplace.entity.Post;
import com.springboot.publicplace.entity.QPost;
import com.springboot.publicplace.repository.queryDSL.PostRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public PostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findByTitleContainingAndCategory(String title, String category, Pageable pageable) {
        QPost post = QPost.post;

        List<Post> results = jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.title.containsIgnoreCase(title),
                        post.category.eq(category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.title.containsIgnoreCase(title),
                        post.category.eq(category)
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, count);
    }

    @Override
    public Page<Post> findByTitleContaining(String title, Pageable pageable) {
        QPost post = QPost.post;

        List<Post> results = jpaQueryFactory
                .selectFrom(post)
                .where(post.title.containsIgnoreCase(title))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = jpaQueryFactory
                .selectFrom(post)
                .where(post.title.containsIgnoreCase(title))
                .fetchCount();

        return new PageImpl<>(results, pageable, count);
    }
}
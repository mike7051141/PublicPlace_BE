package com.springboot.publicplace.repository.queryDSL.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.publicplace.entity.QTeamBoardComment;
import com.springboot.publicplace.entity.TeamBoardComment;
import com.springboot.publicplace.repository.queryDSL.TeamBoardCommentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
public class TeamBoardCommentRepositoryImpl implements TeamBoardCommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public TeamBoardCommentRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<TeamBoardComment> findCommentsByTeamBoardIdOrderByCreatedAt(Long teamBoardId) {
        QTeamBoardComment teamBoardComment = QTeamBoardComment.teamBoardComment;
        return jpaQueryFactory
                .selectFrom(teamBoardComment)
                .where(teamBoardComment.teamBoard.teamBoardId.eq(teamBoardId))
                .orderBy(teamBoardComment.createdAt.asc())
                .fetch();
    }
}

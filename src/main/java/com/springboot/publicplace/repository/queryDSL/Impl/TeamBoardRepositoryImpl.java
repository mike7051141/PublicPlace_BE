package com.springboot.publicplace.repository.queryDSL.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.publicplace.entity.QTeamBoard;
import com.springboot.publicplace.entity.TeamBoard;
import com.springboot.publicplace.repository.queryDSL.TeamBoardRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class TeamBoardRepositoryImpl implements TeamBoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public TeamBoardRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<TeamBoard> findTeamBoardsByTeamIdAndContent(Long teamId, String content, Pageable pageable) {
        QTeamBoard teamBoard = QTeamBoard.teamBoard;

        var query = jpaQueryFactory
                .selectFrom(teamBoard)
                .where(teamBoard.team.teamId.eq(teamId));

        // content가 있는 경우 조건 추가
        if (content != null && !content.isEmpty()) {
            query.where(teamBoard.content.contains(content));
        }

        // 정렬과 페이징 적용
        List<TeamBoard> teamBoards = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(teamBoard.createdAt.desc())
                .fetch();

        // 전체 개수 조회
        long total = jpaQueryFactory
                .select(teamBoard.count())
                .from(teamBoard)
                .where(teamBoard.team.teamId.eq(teamId)
                        .and(content != null && !content.isEmpty() ? teamBoard.content.contains(content) : null))
                .fetchOne();

        return new PageImpl<>(teamBoards, pageable, total);
    }
}

package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.TeamBoardComment;
import com.springboot.publicplace.repository.queryDSL.TeamBoardCommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamBoardCommentRepository extends JpaRepository<TeamBoardComment, Long>, TeamBoardCommentRepositoryCustom {
//    List<TeamBoardComment> findByTeamBoard_TeamBoardIdOrderByCreatedAt(Long teamBoardId);
}

package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.TeamBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamBoardCommentRepository extends JpaRepository<TeamBoardComment, Long> {
    List<TeamBoardComment> findByTeamBoard_TeamBoardIdOrderByCreatedAt(Long teamBoardId);
}

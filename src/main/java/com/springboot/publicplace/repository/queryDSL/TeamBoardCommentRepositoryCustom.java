package com.springboot.publicplace.repository.queryDSL;

import com.springboot.publicplace.entity.TeamBoardComment;

import java.util.List;

public interface TeamBoardCommentRepositoryCustom {
    List<TeamBoardComment> findCommentsByTeamBoardIdOrderByCreatedAt(Long teamBoardId);
}

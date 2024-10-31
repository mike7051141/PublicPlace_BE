package com.springboot.publicplace.repository.queryDSL;

import com.springboot.publicplace.entity.TeamBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamBoardRepositoryCustom {
    Page<TeamBoard> findTeamBoardsByTeamIdAndContent(Long teamId, String content, Pageable pageable);
}
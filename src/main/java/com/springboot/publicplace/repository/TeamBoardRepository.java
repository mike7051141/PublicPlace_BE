package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.TeamBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamBoardRepository extends JpaRepository<TeamBoard, Long> {
    Page<TeamBoard> findByTeam_TeamId(Long teamId, Pageable pageable);
    Page<TeamBoard> findByTeam_TeamIdAndContentContaining(Long teamId, String content, Pageable pageable);
}

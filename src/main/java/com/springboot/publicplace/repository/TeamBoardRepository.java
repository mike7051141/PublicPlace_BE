package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.TeamBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamBoardRepository extends JpaRepository<TeamBoard, Long> {
    
}

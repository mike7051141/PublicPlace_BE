package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // 팀명을 포함하는 검색
    List<Team> findByTeamNameContaining(String teamName);

    boolean existsByTeamName(String teamName);
}

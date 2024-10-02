package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // 팀 멤버 수에 따라 내림차순으로 정렬된 팀 목록을 반환하는 쿼리
    @Query("SELECT t FROM Team t LEFT JOIN t.teamUsers tu GROUP BY t ORDER BY COUNT(tu) DESC")
    List<Team> findAllByMemberCountDesc();

    // 팀명을 포함하는 검색
    List<Team> findByTeamNameContaining(String teamName);


    boolean existsByTeamName(String teamName);
}

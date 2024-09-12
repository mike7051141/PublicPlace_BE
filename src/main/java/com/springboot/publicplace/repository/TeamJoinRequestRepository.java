package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    boolean existsByTeamAndUserAndStatus(Team team, User user, String status);
    List<TeamJoinRequest> findAllByTeamAndStatus(Team team, String status);
    List<TeamJoinRequest> findByUser(User user);
    TeamJoinRequest findByTeamAndUser(Team team, User user);

}

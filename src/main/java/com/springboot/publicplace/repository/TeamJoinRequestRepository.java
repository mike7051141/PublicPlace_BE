package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Status;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    boolean existsByTeamAndUserAndStatus(Team team, User user, Status status);

    List<TeamJoinRequest> findAllByTeamAndStatus(Team team, Status status);

    List<TeamJoinRequest> findByUser(User user);

    TeamJoinRequest findByTeamAndUser(Team team, User user);

    List<TeamJoinRequest> findAllByStatusAndUpdatedAtBefore(Status status, LocalDateTime onWeekAgo);

}

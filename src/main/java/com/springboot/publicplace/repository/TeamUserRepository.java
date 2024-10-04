package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser,Long> {
    boolean existsByTeamAndUser(Team team, User user);

    Optional<TeamUser> findByTeamAndUser(Team team, User user);

    List<TeamUser> findByUser(User user);
}

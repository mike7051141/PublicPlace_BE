package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser,Long> {
}

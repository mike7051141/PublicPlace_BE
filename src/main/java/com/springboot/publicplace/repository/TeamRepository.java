package com.springboot.publicplace.repository;

import com.springboot.publicplace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team,Long> {
}

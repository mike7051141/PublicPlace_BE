package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface TeamService{
    ResultDto createTeam(HttpServletRequest request, TeamRequestDto teamRequestDto);
}

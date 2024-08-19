package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface TeamService{
    ResultDto createTeam(HttpServletRequest request, TeamRequestDto teamRequestDto);
    ResultDto updateTeam(Long teamId, HttpServletRequest request, TeamRequestDto teamRequestDto);
    TeamResponseDto getTeamInfo(Long teamId);
}

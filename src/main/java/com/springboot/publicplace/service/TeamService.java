package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.TeamListResponseDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;
import com.springboot.publicplace.entity.Team;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public interface TeamService{
    ResultDto createTeam(HttpServletRequest request, TeamRequestDto teamRequestDto);

    ResultDto updateTeam(Long teamId, HttpServletRequest request, TeamRequestDto teamRequestDto);

    TeamResponseDto getTeamInfo(Long teamId);

    List<TeamResponseDto> getTeamList(HttpServletRequest servletRequest);

    List<TeamListResponseDto> getTeamsByCriteria(String sortBy);

}

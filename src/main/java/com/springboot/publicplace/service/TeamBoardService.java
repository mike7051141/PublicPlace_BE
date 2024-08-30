package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardRequestDto;
import com.springboot.publicplace.dto.response.TeamBoardDetailResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface TeamBoardService {
    ResultDto createTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamId);

    ResultDto updateTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamBoardId);

    TeamBoardDetailResponseDto getTeamBoardDetail(HttpServletRequest servletRequest, Long teamBoardId);

    ResultDto deleteTeamBoard(HttpServletRequest servletRequest, Long teamBoardId);
}

package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardRequestDto;
import com.springboot.publicplace.dto.response.TeamBoardDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamBoardListResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamBoardService {
    ResultDto createTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamId);

    ResultDto updateTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamBoardId);

    TeamBoardDetailResponseDto getTeamBoardDetail(HttpServletRequest servletRequest, Long teamBoardId);

    List<TeamBoardListResponseDto> getTeamBoardList(HttpServletRequest servletRequest, Long teamId, String content, int page, int size);

    ResultDto deleteTeamBoard(HttpServletRequest servletRequest, Long teamBoardId);
}


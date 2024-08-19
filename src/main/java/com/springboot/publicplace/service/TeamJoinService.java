package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamJoinService {

    ResultDto joinTeam(Long teamId, HttpServletRequest servletRequest, String joinReason);

    List<TeamJoinResponseDto> getJoinRequestList(Long teamId, HttpServletRequest servletRequest);

    TeamJoinDetailResponseDto getJoinRequestDetail(Long requestId, HttpServletRequest servletRequest);

    ResultDto approveJoinRequest(Long requestId, HttpServletRequest servletRequest);

    ResultDto rejectJoinRequest(Long requestId, HttpServletRequest servletRequest);
}

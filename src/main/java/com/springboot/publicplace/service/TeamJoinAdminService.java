package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamJoinAdminService {
    List<TeamJoinResponseDto> getJoinRequestList(Long teamId, HttpServletRequest servletRequest);

    TeamJoinDetailResponseDto getJoinRequestDetail(Long requestId, HttpServletRequest servletRequest);

    ResultDto processJoinRequest(Long requestId, HttpServletRequest servletRequest, boolean accept);
}

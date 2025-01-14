package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TeamJoinService {
    ResultDto joinTeam(Long teamId, HttpServletRequest servletRequest, String joinReason);

    ResultDto leaveTeam(Long teamId, HttpServletRequest servletRequest);

}

package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.KakaoUserUpdateDto;
import com.springboot.publicplace.dto.request.LocalUserUpdateDto;
import com.springboot.publicplace.dto.response.MyPageTeamResponseDto;
import com.springboot.publicplace.dto.response.UserResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    UserResponseDto getUser(HttpServletRequest servletRequest);

    ResultDto updateKakaoUser(HttpServletRequest servletRequest, KakaoUserUpdateDto kakaoUserUpdateDto);

    ResultDto updateLocalUser (HttpServletRequest servletRequest, LocalUserUpdateDto localUserUpdateDto);

    List<MyPageTeamResponseDto> getJoinedTeams(HttpServletRequest servletRequest);

    List<MyPageTeamResponseDto> getAppliedTeams(HttpServletRequest servletRequest);

    ResultDto deleteTeamJoinRequest(HttpServletRequest servletRequest, Long teamId);
}

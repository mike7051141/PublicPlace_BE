package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.UserUpdateDto;
import com.springboot.publicplace.dto.response.MyPageTeamResponseDto;
import com.springboot.publicplace.dto.response.UserResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    UserResponseDto getUser(HttpServletRequest servletRequest);

    ResultDto updateUser(HttpServletRequest servletRequest, UserUpdateDto userUpdateDto);

    List<MyPageTeamResponseDto> getJoinedTeams(HttpServletRequest servletRequest);

    List<MyPageTeamResponseDto> getAppliedTeams(HttpServletRequest servletRequest);
}

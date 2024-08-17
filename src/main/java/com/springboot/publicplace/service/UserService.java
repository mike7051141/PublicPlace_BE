package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.UserUpdateDto;
import com.springboot.publicplace.dto.response.UserResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    UserResponseDto getUser(HttpServletRequest servletRequest);

    ResultDto updateUser(HttpServletRequest servletRequest, UserUpdateDto userUpdateDto);
}

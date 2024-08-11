package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.SignDto.SignInResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;

public interface SignService {
    SignUpResultDto SignUp(SignUpDto signUpDto, String roles);
    SignInResultDto SignIn(String account, String password) throws RuntimeException;
}
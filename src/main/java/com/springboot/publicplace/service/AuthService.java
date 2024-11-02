package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.SignDto.JwtResponse;

public interface AuthService {
    JwtResponse refreshAccessToken(String refreshToken);
}

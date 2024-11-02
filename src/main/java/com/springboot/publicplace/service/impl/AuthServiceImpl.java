package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.SignDto.JwtResponse;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.InvalidCredentialsException;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtResponse refreshAccessToken(String refreshToken) {
        if(!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }
        String email = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
        User user = userRepository.findByEmail(email);

        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
        String newRefreshToken = jwtTokenProvider.refreshIfExpired(user.getEmail(), user.getRefreshToken());

        return new JwtResponse(true, HttpStatus.OK.value(), "Token refreshed successfully", newAccessToken, newRefreshToken);
    }

}

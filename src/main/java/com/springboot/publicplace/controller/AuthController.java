package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.SignDto.JwtResponse;
import com.springboot.publicplace.dto.request.RefreshTokenRequestDto;
import com.springboot.publicplace.exception.InvalidCredentialsException;
import com.springboot.publicplace.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto) {
        try {
            JwtResponse jwtResponse = authService.refreshAccessToken(requestDto.getRefreshToken());
            return ResponseEntity.ok(jwtResponse);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to refresh token");
        }
    }
}

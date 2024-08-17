package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.UserUpdateDto;
import com.springboot.publicplace.dto.response.UserResponseDto;
import com.springboot.publicplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getUser")
    public ResponseEntity<UserResponseDto> getUser(HttpServletRequest servletRequest) {
        UserResponseDto user = userService.getUser(servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ResultDto> updateUser(HttpServletRequest servletRequest,@RequestBody UserUpdateDto userUpdateDto) {
        ResultDto resultDto = userService.updateUser(servletRequest, userUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}

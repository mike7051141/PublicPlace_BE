package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.KakaoUserUpdateDto;
import com.springboot.publicplace.dto.request.LocalUserUpdateDto;
import com.springboot.publicplace.dto.response.MyPageTeamResponseDto;
import com.springboot.publicplace.dto.response.UserResponseDto;
import com.springboot.publicplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PutMapping("/updateKakaoUser")
    public ResponseEntity<ResultDto> updateKakaoUser(HttpServletRequest servletRequest,
                                                     @RequestBody KakaoUserUpdateDto kakaoUserUpdateDto) {
        ResultDto resultDto = userService.updateKakaoUser(servletRequest, kakaoUserUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @PutMapping("/updateLocalUser")
    public ResponseEntity<ResultDto> updateLocalUser(HttpServletRequest servletRequest,
                                                @RequestBody LocalUserUpdateDto localUserUpdateDto) {
        ResultDto resultDto = userService.updateLocalUser(servletRequest, localUserUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @GetMapping("/joinedTeams")
    public ResponseEntity<List<MyPageTeamResponseDto>> getJoinedTeams(HttpServletRequest servletRequest) {
        List<MyPageTeamResponseDto> myPageTeamResponseDtos = userService.getJoinedTeams(servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(myPageTeamResponseDtos);
    }
    @GetMapping("/appliedTeams")
    public ResponseEntity<List<MyPageTeamResponseDto>> getAppliedTeams(HttpServletRequest servletRequest) {
        List<MyPageTeamResponseDto> myPageTeamResponseDtos = userService.getAppliedTeams(servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(myPageTeamResponseDtos);
    }
    @DeleteMapping("/deleteTeamJoinRequest")
    public ResponseEntity<ResultDto> deleteTeamJoinRequest(HttpServletRequest servletRequest, Long teamId ) {
        ResultDto resultDto = userService.deleteTeamJoinRequest(servletRequest, teamId);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}

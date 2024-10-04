package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;
import com.springboot.publicplace.service.TeamJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TeamJoinController {

    private final TeamJoinService teamJoinService;

    @PostMapping("/joinTeam/{teamId}")
    public ResponseEntity<ResultDto> joinTeam(@PathVariable Long teamId,
                                              HttpServletRequest servletRequest,
                                              String joinReason) {
        ResultDto resultDto = teamJoinService.joinTeam(teamId, servletRequest,joinReason);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    // 팀 탈퇴 요청 엔드포인트
    @DeleteMapping("/leaveTeam/{teamId}")
    public ResponseEntity<ResultDto> leaveTeam(@PathVariable Long teamId,
                                               HttpServletRequest servletRequest) {
        ResultDto result = teamJoinService.leaveTeam(teamId, servletRequest);
        return ResponseEntity.ok(result);
    }

}

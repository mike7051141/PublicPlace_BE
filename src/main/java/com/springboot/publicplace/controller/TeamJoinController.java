package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;
import com.springboot.publicplace.service.TeamJoinService;
import com.springboot.publicplace.service.TeamService;
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
    @DeleteMapping("/leave/{teamId}")
    public ResponseEntity<ResultDto> leaveTeam(@PathVariable Long teamId,
                                               HttpServletRequest servletRequest) {
        ResultDto result = teamJoinService.leaveTeam(teamId, servletRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/joinRequestList/{teamId}")
    public ResponseEntity<List<TeamJoinResponseDto>> getJoinRequestList(@PathVariable Long teamId,
                                                                        HttpServletRequest servletRequest) {
        List<TeamJoinResponseDto> joinRequests = teamJoinService.getJoinRequestList(teamId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(joinRequests);
    }

    @GetMapping("/joinRequestDetail/{requestId}")
    public ResponseEntity<TeamJoinDetailResponseDto> getJoinRequestDetail(@PathVariable Long requestId,
                                                                          HttpServletRequest servletRequest) {
        TeamJoinDetailResponseDto detailDto = teamJoinService.getJoinRequestDetail(requestId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(detailDto);
    }

    @PostMapping("/joinRequest/{requestId}/approve")
    public ResponseEntity<ResultDto> approveJoinRequest(@PathVariable Long requestId,
                                                        HttpServletRequest servletRequest) {
        ResultDto result = teamJoinService.approveJoinRequest(requestId, servletRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/joinRequest/{requestId}/reject")
    public ResponseEntity<ResultDto> rejectJoinRequest(@PathVariable Long requestId,
                                                       HttpServletRequest servletRequest) {
        ResultDto result = teamJoinService.rejectJoinRequest(requestId, servletRequest);
        return ResponseEntity.ok(result);
    }

}

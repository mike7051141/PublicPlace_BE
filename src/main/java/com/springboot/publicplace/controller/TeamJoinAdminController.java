package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;
import com.springboot.publicplace.service.TeamJoinAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/joinRequest")
public class TeamJoinAdminController {

    private final TeamJoinAdminService teamJoinAdminService;

    @GetMapping("/list/{teamId}")
    public ResponseEntity<List<TeamJoinResponseDto>> getJoinRequestList(@PathVariable Long teamId,
                                                                        HttpServletRequest servletRequest) {
        List<TeamJoinResponseDto> joinRequests = teamJoinAdminService.getJoinRequestList(teamId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(joinRequests);
    }

    @GetMapping("/detail/{requestId}")
    public ResponseEntity<TeamJoinDetailResponseDto> getJoinRequestDetail(@PathVariable Long requestId,
                                                                          HttpServletRequest servletRequest) {
        TeamJoinDetailResponseDto detailDto = teamJoinAdminService.getJoinRequestDetail(requestId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(detailDto);
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ResultDto> approveJoinRequest(@PathVariable Long requestId,
                                                        HttpServletRequest servletRequest) {
        ResultDto result = teamJoinAdminService.approveJoinRequest(requestId, servletRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ResultDto> rejectJoinRequest(@PathVariable Long requestId,
                                                       HttpServletRequest servletRequest) {
        ResultDto result = teamJoinAdminService.rejectJoinRequest(requestId, servletRequest);
        return ResponseEntity.ok(result);
    }
}

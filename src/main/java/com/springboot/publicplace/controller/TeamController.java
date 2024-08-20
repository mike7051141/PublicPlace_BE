package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;
import com.springboot.publicplace.service.TeamJoinService;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    private final TeamJoinService teamJoinService;

    @PostMapping("/createTeam")
    public ResponseEntity<ResultDto> createTeam(@RequestBody TeamRequestDto requestDto,
                                                HttpServletRequest servletRequest) {
        ResultDto resultDto = teamService.createTeam(servletRequest, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @PutMapping("/updateTeam")
    public ResponseEntity<ResultDto> updateTeam(@RequestParam Long teamId,
                                                HttpServletRequest servletRequest,
                                                @RequestBody TeamRequestDto requestDto) {
        ResultDto resultDto = teamService.updateTeam(teamId, servletRequest, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @GetMapping("/getTeam/{teamId}")
    public ResponseEntity<TeamResponseDto> getTeam(@PathVariable Long teamId) {
        TeamResponseDto teamInfo = teamService.getTeamInfo(teamId);
        return ResponseEntity.ok(teamInfo);
    }

}

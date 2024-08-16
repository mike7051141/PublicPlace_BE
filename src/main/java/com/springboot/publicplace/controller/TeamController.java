package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/createTeam")
    public ResponseEntity<ResultDto> createTeam(@RequestBody TeamRequestDto requestDto, HttpServletRequest servletRequest) {
        ResultDto resultDto = teamService.createTeam(servletRequest, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

}

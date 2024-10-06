package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardRequestDto;
import com.springboot.publicplace.dto.response.TeamBoardDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamBoardListResponseDto;
import com.springboot.publicplace.service.TeamBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/team-board")
@RequiredArgsConstructor
public class TeamBoardController {

    private final TeamBoardService teamBoardService;

    @PostMapping("/create/{teamId}")
    ResponseEntity<ResultDto> createTeamBoard(HttpServletRequest servletRequest,
                                              @PathVariable Long teamId,
                                              @RequestBody TeamBoardRequestDto requestDto) {
        ResultDto resultDto = teamBoardService.createTeamBoard(servletRequest, requestDto, teamId);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @PutMapping("/update/{teamBoardId}")
    ResponseEntity<ResultDto> updateTeamBoard(HttpServletRequest servletRequest,
                                              @PathVariable Long teamBoardId,
                                              @RequestBody TeamBoardRequestDto requestDto) {
        ResultDto resultDto = teamBoardService.updateTeamBoard(servletRequest, requestDto, teamBoardId);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @DeleteMapping("/delete/{teamBoardId}")
    ResponseEntity<ResultDto> deleteTeamBoard(HttpServletRequest servletRequest,
                                              @PathVariable Long teamBoardId) {
        ResultDto resultDto = teamBoardService.deleteTeamBoard(servletRequest, teamBoardId);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @GetMapping("/getDetail/{teamBoardId}")
    ResponseEntity<TeamBoardDetailResponseDto> getTeamBoardDetail(HttpServletRequest servletRequest,
                                                                  @PathVariable Long teamBoardId) {
        TeamBoardDetailResponseDto teamBoardDetailResponseDto = teamBoardService.getTeamBoardDetail(servletRequest, teamBoardId);
        return ResponseEntity.status(HttpStatus.OK).body(teamBoardDetailResponseDto);
    }

    @GetMapping("/getList/{teamId}")
    ResponseEntity<List<TeamBoardListResponseDto>> getTeamBoardList(HttpServletRequest servletRequest,
                                                                      @RequestParam Long teamId,
                                                                      @RequestParam(required = false) String content,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        List<TeamBoardListResponseDto> teamBoardList = teamBoardService.getTeamBoardList(servletRequest, teamId, content, page - 1, size);
        return ResponseEntity.status(HttpStatus.OK).body(teamBoardList);
    }
}


package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardCommentRequestDto;
import com.springboot.publicplace.service.TeamBoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/team-board/comment")
@RequiredArgsConstructor
public class TeamBoardCommentController {
    private final TeamBoardCommentService teamBoardCommentService;

    @PostMapping("/create/{teamBoardId}")
    ResponseEntity<ResultDto> createComment(HttpServletRequest servletRequest,
                                            @PathVariable Long teamBoardId,
                                            @RequestBody TeamBoardCommentRequestDto requestDto) {
        ResultDto resultDto = teamBoardCommentService.createComment(servletRequest, teamBoardId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @DeleteMapping("/delete/{commentId}")
    ResponseEntity<ResultDto> deleteComment(HttpServletRequest servletRequest,
                                            @PathVariable Long commentId) {
        ResultDto resultDto = teamBoardCommentService.deleteComment(servletRequest, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}

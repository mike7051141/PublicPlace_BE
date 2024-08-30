package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardCommentRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface TeamBoardCommentService {

    ResultDto createComment(HttpServletRequest servletRequest, Long teamBoardId, TeamBoardCommentRequestDto requestDto);

    ResultDto deleteComment(HttpServletRequest servletRequest, Long commentId);

}

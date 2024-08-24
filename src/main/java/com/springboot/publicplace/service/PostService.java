package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.CommentRequestDto;
import com.springboot.publicplace.dto.request.PostRequestDto;
import com.springboot.publicplace.dto.response.PostDetailResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostService {
    ResultDto createPost(HttpServletRequest servletRequest, PostRequestDto postRequestDto);

    ResultDto updatePost(Long postId, HttpServletRequest servletRequest, PostRequestDto postRequestDto);

    ResultDto deletePost(Long postId, HttpServletRequest servletRequest);

    PostDetailResponseDto getPostDetails(Long postId, HttpServletRequest servletRequest);

    ResultDto createComment(HttpServletRequest servletRequest, CommentRequestDto commentRequestDto);

    ResultDto deleteComment(Long commentId, HttpServletRequest servletRequest);

    ResultDto toggleLike(Long postId, HttpServletRequest servletRequest);

//    List<CommentResponseDto> getCommentsByPost(Long postId);
}

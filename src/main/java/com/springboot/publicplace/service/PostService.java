package com.springboot.publicplace.service;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.PostRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface PostService {
    ResultDto createPost(HttpServletRequest servletRequest, PostRequestDto postRequestDto);

    ResultDto updatePost(Long postId, HttpServletRequest servletRequest, PostRequestDto postRequestDto);

    ResultDto deletePost(Long postId, HttpServletRequest servletRequest);
}

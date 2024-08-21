package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.PostRequestDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/createPost")
    public ResponseEntity<ResultDto> createPost(@RequestBody PostRequestDto requestDto,
                                                HttpServletRequest servletRequest) {
        ResultDto resultDto = postService.createPost(servletRequest, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @PutMapping("/updatePost/{postId}")
    public ResponseEntity<ResultDto> updatePost(@PathVariable Long postId,
                                                HttpServletRequest servletRequest,
                                                @RequestBody PostRequestDto requestDto) {
        ResultDto resultDto = postService.updatePost(postId, servletRequest, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<ResultDto> deletePost(@PathVariable Long postId,
                                               HttpServletRequest servletRequest) {
        ResultDto resultDto = postService.deletePost(postId, servletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}

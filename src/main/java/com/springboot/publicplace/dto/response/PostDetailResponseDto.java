package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDto {
    private String title;
    private String category;
    private String content;
    private String postImg;
    private String authorNickname;
    private LocalDateTime createdDate;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<PostCommentResponseDto> comments;
}

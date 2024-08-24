package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostListResponseDto {
    private Long postId;
    private String title;
    private String category;
    private String authorNickname;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdDate;
}

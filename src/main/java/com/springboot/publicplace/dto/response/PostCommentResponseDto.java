package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponseDto {
    private Long commentId;
    private String profileImg;
    private String nickname;
    private String content;
    private LocalDateTime createdDate;
}

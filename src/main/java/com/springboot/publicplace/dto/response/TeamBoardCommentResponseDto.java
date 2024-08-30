package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamBoardCommentResponseDto {
    private Long commentId;
    private String content;
    private String userName;
    private String profileImg;
    private LocalDateTime createdDate;
}

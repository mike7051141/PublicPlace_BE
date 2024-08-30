package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamBoardDetailResponseDto {
    private String content;
    private String Image;
    private String userName;
    private LocalDateTime createdDate;
//    private int commentCount;
//    private List<PostCommentResponseDto> comments;
}

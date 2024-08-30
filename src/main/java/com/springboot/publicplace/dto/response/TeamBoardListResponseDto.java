package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamBoardListResponseDto {
    private Long teamBoardId;
    private String content;
    private String Image;
    private String userName;
    private String matchLocation;
    private LocalDateTime createdDate;
}

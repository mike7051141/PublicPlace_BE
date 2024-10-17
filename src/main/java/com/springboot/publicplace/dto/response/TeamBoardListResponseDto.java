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
    private int commentCount;
    private String Image;
    private String userName;
    private String userProfileImage;
    private String matchLocation;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdDate;
}

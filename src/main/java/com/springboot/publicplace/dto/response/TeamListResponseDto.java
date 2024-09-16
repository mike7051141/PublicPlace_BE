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
public class TeamListResponseDto {
    private Long teamId;
    private String teamName;
    private LocalDateTime createdAt;
    private String teamLocation;
    private String teamImg;
    private Long teamMemberCount;
    private double averageAge;
}

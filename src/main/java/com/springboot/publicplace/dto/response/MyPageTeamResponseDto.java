package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageTeamResponseDto {
    private Long teamId;
    private String teamName;
    private String teamImg;
    private Long teamMembers;
    private String teamLocation;
    private String teamCreationDate;
}

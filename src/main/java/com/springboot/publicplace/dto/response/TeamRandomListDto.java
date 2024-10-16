package com.springboot.publicplace.dto.response;

import com.springboot.publicplace.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRandomListDto {
    private Long teamId;
    private String teamName;
    private LocalDateTime createdAt;
    private String teamLocation;
    private String teamImg;
    private Long teamMemberCount;
    private double averageAge;
}

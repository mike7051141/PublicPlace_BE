package com.springboot.publicplace.dto.response;

import com.springboot.publicplace.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GPTTeamListDto {
    private String teamName;
    private String teamInfo;
    private LocalDateTime createdAt;
    private String teamLocation;
    private List<String> activityDays;
    private Long teamMemberCount;
    private Double averageAge;
}

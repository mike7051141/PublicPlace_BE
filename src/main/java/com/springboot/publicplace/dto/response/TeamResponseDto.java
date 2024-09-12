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
public class TeamResponseDto {
    private Long teamId;
    private String teamName;
    private String teamInfo;

    private LocalDateTime createdAt;
    private String teamLocation;
    private String teamImg;
    private List<String> activityDays;
    private Long teamMemberCount;
    private List<MemberDto> members;  // 팀원 리스트
}

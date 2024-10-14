package com.springboot.publicplace.dto.response;

import com.springboot.publicplace.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRoleResponseDto {
    private RoleType role;
    private boolean isLeader;
}

package com.springboot.publicplace.dto;

import com.springboot.publicplace.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String name;
    private String nickname;
    private String position;
    private RoleType role;  // 팀 내 역할 (예: 회장, 팀원 등)
    private String ageRange;
}
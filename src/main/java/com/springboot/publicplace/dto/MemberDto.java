package com.springboot.publicplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private String name;
    private String nickname;
    private String position;
    private String role;  // 팀 내 역할 (예: 회장, 팀원 등)
    private String ageRange;
}
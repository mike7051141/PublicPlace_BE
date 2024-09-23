package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String profileImg;
    private String gender;
    private String ageRange;
    private String createdAt;
    private String updatedAt;
    private String foot;
    private String position;
    private String loginApproach;
}
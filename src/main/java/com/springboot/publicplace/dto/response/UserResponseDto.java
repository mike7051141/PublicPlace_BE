package com.springboot.publicplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {
    private Long uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String profileImg;
    private String gender;
    private String ageRange;
}
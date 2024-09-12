package com.springboot.publicplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserUpdateDto {
    private String phoneNumber;
    private String nickname;
    private String profileImg;
    private String gender;
    private String foot;
    private String position;
}

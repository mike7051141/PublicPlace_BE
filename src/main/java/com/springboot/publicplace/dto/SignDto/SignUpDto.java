package com.springboot.publicplace.dto.SignDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignUpDto {
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String profileImg;
    private String gender;
    private String ageRange;
}

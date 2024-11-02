package com.springboot.publicplace.dto.SignDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignInResultDto{
    private boolean success;
    private int code;
    private String msg;
    private String token;
    private String refreshToken;
}

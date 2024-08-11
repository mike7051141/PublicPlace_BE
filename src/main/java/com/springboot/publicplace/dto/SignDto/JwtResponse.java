package com.springboot.publicplace.dto.SignDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private boolean success;
    private int code;
    private String msg;
    private String token;
}
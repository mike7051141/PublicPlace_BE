package com.springboot.publicplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamJoinRequestDto {
    private String name;

    private String phoneNumber;

    private String gender;

    private String ageRange;

    private String foot;

    private String position;

    private String joinReason;
}

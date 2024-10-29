package com.springboot.publicplace.dto.response;

import com.springboot.publicplace.entity.RoleType;
import com.springboot.publicplace.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamJoinDetailResponseDto {
    private Long requestId;
    private String userName;
    private String userGender;
    private String userAgeRange;
    private String userPhoneNumber;
    private String foot;
    private String position;
    private String joinReason;
    private Status status;
    private RoleType role;
}

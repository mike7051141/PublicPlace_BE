package com.springboot.publicplace.dto.response;

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
    private String joinReason;
    private String status;
    private String role;
    private LocalDateTime requestDate;
}

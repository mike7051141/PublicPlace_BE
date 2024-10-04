package com.springboot.publicplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDto {

    private String teamName;

    private String teamInfo;

    private String teamLocation;

    private Double latitude;

    private Double longitude;

    private String teamImg;

    private List<String> activityDays;

}

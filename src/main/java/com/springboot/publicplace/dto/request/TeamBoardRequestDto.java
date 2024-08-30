package com.springboot.publicplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamBoardRequestDto {
    private String content;
    private String Image;
    private String matchLocation;
}

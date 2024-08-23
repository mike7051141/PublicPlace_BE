package com.springboot.publicplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;
    private String category;
    private String content;
    private String postImg;
}

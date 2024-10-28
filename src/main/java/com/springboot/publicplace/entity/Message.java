package com.springboot.publicplace.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Lob;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;
}

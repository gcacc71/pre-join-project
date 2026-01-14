package com.ttn.practice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailRequestDto {
    private String subject;
    private String to;
    private String text;
}

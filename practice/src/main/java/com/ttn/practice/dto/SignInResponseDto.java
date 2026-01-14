package com.ttn.practice.dto;

import com.ttn.practice.util.ROLE;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SignInResponseDto {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String role;
}


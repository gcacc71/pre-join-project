package com.ttn.practice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequestDto {
    @NotBlank(message = "Email is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    private String password;
}


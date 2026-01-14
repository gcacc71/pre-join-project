package com.ttn.practice.dto;

import com.ttn.practice.util.ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    @Email(message = "Email is not in valid format!")
    @NotBlank(message = "Email is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, message = "Password must have atleast 8 characters!")
    @Size(max = 20, message = "Password can have have atmost 20 characters!")
    private String password;

    @NotBlank(message = "Full name is required!")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;
}


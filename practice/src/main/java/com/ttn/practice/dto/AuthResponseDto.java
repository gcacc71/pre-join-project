package com.ttn.practice.dto;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
public class AuthResponseDto<T> {
    private String status;
    private String message;
    private T response;
}

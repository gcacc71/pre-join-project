package com.ttn.practice.controller;

import com.ttn.practice.dto.AuthResponseDto;
import com.ttn.practice.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping()
    public ResponseEntity<AuthResponseDto<?>> test(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(AuthResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK.value()))
                        .message("Securing Spring Boot using Spring Security and JWT")
                        .response(new User())
                        .build());
    }
}

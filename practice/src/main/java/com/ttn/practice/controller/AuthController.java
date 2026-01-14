package com.ttn.practice.controller;


import com.ttn.practice.dto.AuthResponseDto;
import com.ttn.practice.dto.SignInRequestDto;
import com.ttn.practice.dto.SignUpRequestDto;
import com.ttn.practice.exception.RoleNotFoundException;
import com.ttn.practice.exception.UserAlreadyExistsException;
import com.ttn.practice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDto<?>> register(@RequestBody @Valid SignUpRequestDto signUpRequestDto)
        throws UserAlreadyExistsException, RoleNotFoundException {
        return authService.sighUp(signUpRequestDto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto<?>> login(@RequestBody @Valid SignInRequestDto signInRequestDto) {
        return authService.signIn(signInRequestDto);
    }


}

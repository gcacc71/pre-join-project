package com.ttn.practice.service;

import com.ttn.practice.dto.AuthResponseDto;
import com.ttn.practice.dto.SignInRequestDto;
import com.ttn.practice.dto.SignUpRequestDto;
import com.ttn.practice.exception.RoleNotFoundException;
import com.ttn.practice.exception.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService{
    ResponseEntity<AuthResponseDto<?>> sighUp(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, RoleNotFoundException;

    ResponseEntity<AuthResponseDto<?>> signIn(SignInRequestDto signInRequestDto);
}

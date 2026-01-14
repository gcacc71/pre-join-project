package com.ttn.practice.service.impl;

import com.ttn.practice.dto.AuthResponseDto;
import com.ttn.practice.dto.SignInRequestDto;
import com.ttn.practice.dto.SignInResponseDto;
import com.ttn.practice.dto.SignUpRequestDto;
import com.ttn.practice.exception.RoleNotFoundException;
import com.ttn.practice.exception.UserAlreadyExistsException;
import com.ttn.practice.model.User;
import com.ttn.practice.security.jwt.JwtUtils;
import com.ttn.practice.security.service.UserDetailsImpl;
import com.ttn.practice.service.AuthService;
import com.ttn.practice.service.UserService;
import com.ttn.practice.util.ROLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<AuthResponseDto<?>> sighUp(SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException, RoleNotFoundException {

        // (1) check if the username is existed, if yes, throw the exception
        if (userService.existByUsername(signUpRequestDto.getUsername())) {
            throw new UserAlreadyExistsException("Registration Failed: Provided username already exists. Try sign in or provide another username.");
        }

        // (2) if not, create new user
        User user = createUser(signUpRequestDto);

        // (3) save user to db
        userService.save(user);

        // (4) return the result
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponseDto.builder()
                        .status(String.valueOf(HttpStatus.CREATED))
                        .message("User account has been successfully created!")
                        .build()
        );
    }

    @Override
    public ResponseEntity<AuthResponseDto<?>> signIn(SignInRequestDto signInRequestDto) {
        // (1) authenticate the username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getUsername(), signInRequestDto.getPassword())
        );

        // (2) put the authentication object to security context for managing
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // (3) create a jwt for authorizing by authentication object
        String jwt = jwtUtils.generateJwtToken(authentication);

        // (4) get the detail of user by authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // (5) get role by user detail
        assert userDetails != null;
        String role = userDetails.getAuthorities().toString();

        // (6) initialize response then return to client
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .username(userDetails.getUsername())
                .id(userDetails.getId().intValue())
                .token(jwt)
                .type("Bearer ")
                .role(role)
                .build();


        return ResponseEntity.status(HttpStatus.OK).body(
                AuthResponseDto.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .message("Sign in successfully!")
                        .response(signInResponseDto)
                        .build()
        );
    }


    // method: create user
    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException{
        return User.builder()
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .enabled(true)
                .role(ROLE.EMPLOYEE.name())
                .fullName(signUpRequestDto.getFullName())
                .createdAt(Instant.now())
                .build();
    }

}

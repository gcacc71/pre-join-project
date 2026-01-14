package com.ttn.practice.controller;


import com.ttn.practice.dto.EmailRequestDto;
import com.ttn.practice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/mail")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody EmailRequestDto emailRequestDto) throws InterruptedException{
        emailService.sendSimpleMessage(emailRequestDto.getTo(), emailRequestDto.getSubject(), emailRequestDto.getText());

        return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully!");
    }
}

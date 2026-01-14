package com.ttn.practice.service.impl;

import com.ttn.practice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Sending email to user...");

        try {
            Thread.sleep(3000);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tonhanlk113@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Email sending interrupted", e);
        }
    }
}

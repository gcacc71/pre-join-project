package com.ttn.practice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // WARNING: Do not commit real passwords to GitHub!
        // Read "Important Security Note" below.
        // Read "Important Security Note" below.
        mailSender.setUsername("tonhanlk113@gmail.com");
        mailSender.setPassword("bnrp gsur zvut unaj");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // set to false in production to avoid cluttering logs
        props.put("mail.debug", "true");

        return mailSender;
    }
}

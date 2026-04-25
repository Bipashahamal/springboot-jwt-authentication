package com.example.employee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Send Welcome Email
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Welcome to Employee Management System");

            message.setText(
                    "Hello " + name + ",\n\n" +
                    "Welcome to our company! 🎉\n\n" +
                    "Your profile has been created successfully.\n\n" +
                    "Regards,\nTeam");

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }
    @Async
    public void sendUpdateEmail(String toEmail, String name) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Profile Update Confirmation");

            message.setText(
                    "Hello " + name + ",\n\n" +
                    "Your profile has been updated successfully.\n\n" +
                    "Regards,\nTeam");

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }
}
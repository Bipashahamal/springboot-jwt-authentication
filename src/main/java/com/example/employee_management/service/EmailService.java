package com.example.employee_management.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async
    public void sendWelcomeEmail(String email, String name) {
        System.out.println("Sending welcome email to: " + email + " (Name: " + name + ")");
        try {
            Thread.sleep(15000); // simulate email sending delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Welcome email sent to " + name + " (" + email + ")");
    }
}
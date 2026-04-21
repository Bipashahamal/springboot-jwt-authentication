package com.example.employee_management.listener;

import com.example.employee_management.event.EmployeeCreatedEvent;
import com.example.employee_management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventListener {

    @Autowired
    private EmailService emailService;

    @Async
    @EventListener
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {

        var emp = event.getEmployee();

        emailService.sendWelcomeEmail(
                emp.getEmail(),
                emp.getFirstName() + " " + emp.getLastName());
    }
}
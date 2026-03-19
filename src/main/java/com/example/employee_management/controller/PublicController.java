package com.example.employee_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Employee Management API");
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Employee Management System");
        response.put("description", "A secure REST API for managing employees with JWT authentication");
        response.put("features", new String[]{
            "User Registration & Authentication",
            "JWT Token-based Security",
            "Role-based Access Control (ADMIN/USER)",
            "Employee CRUD Operations",
            "Async Email Notifications",
            "Scheduled Tasks",
            "Pagination & Sorting"
        });
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
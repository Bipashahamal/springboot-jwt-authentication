package com.example.employee_management.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    @Size(min = 3, message = "Name must be at least 3 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull
    private String role;
}
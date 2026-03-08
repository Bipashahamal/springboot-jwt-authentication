package com.example.employee_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Invalid email")
    private String email;

    @NotNull
    private String password;
}
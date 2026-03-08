package com.example.employee_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Email
    private String email;

    @NotNull
    private String department;

    @Positive(message = "Salary must be positive")
    private Double salary;
}
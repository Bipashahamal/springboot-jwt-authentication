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

    @Positive(message = "Salary must be positive")
    private Double salary;

    @NotNull(message = "Department ID is mandatory")
    private Long departmentId;
}
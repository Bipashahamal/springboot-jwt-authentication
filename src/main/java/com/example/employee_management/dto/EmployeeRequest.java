package com.example.employee_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class EmployeeRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Email
    private String email;

    @NotNull
    @JsonProperty("department")
    private Long departmentId;

    @Positive(message = "Salary must be positive")
    private Double salary;
}
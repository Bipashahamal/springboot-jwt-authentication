package com.example.employee_management.exception;

import java.util.Map;

public class EmployeeValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public EmployeeValidationException(Map<String, String> errors) {
        super("Multiple validation errors occurred");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
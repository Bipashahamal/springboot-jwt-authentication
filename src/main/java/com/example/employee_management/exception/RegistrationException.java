package com.example.employee_management.exception;

import java.util.Map;

public class RegistrationException extends RuntimeException {
    private final Map<String, String> errors;

    public RegistrationException(Map<String, String> errors) {
        super("Multiple registration errors occurred");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}

package com.example.employee_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync // Enable @Async methods
@EnableScheduling // Enable @Scheduled methods
public class AsyncConfig {
    // No extra code needed
}
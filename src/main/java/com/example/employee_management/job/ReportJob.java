package com.example.employee_management.job;

import com.example.employee_management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportJob {

    @Autowired
    private EmployeeService employeeService;

    @Scheduled(cron = "0 0 0 1 * ?") // Run every 1st day of month at midnight
    public void generateMonthlyReport() {
        System.out.println("Generating monthly employee report...");
        employeeService.generateMonthlyReport(); // implement in EmployeeService
    }
}
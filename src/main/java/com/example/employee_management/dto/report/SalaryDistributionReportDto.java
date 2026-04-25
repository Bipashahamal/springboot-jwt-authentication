package com.example.employee_management.dto.report;

public record SalaryDistributionReportDto(
    String departmentName,
    Double totalSalary,
    Double averageSalary,
    Double maxSalary,
    Double minSalary,
    Long employeeCount
) {}

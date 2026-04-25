package com.example.employee_management.dto.report;

public record MonthlyHiringReportDto(
    Integer hiringYear,
    Integer hiringMonth,
    Long employeeCount
) {}

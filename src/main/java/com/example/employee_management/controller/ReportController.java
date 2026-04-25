package com.example.employee_management.controller;

import com.example.employee_management.dto.report.DepartmentEmployeeCountDto;
import com.example.employee_management.dto.report.MonthlyHiringReportDto;
import com.example.employee_management.dto.report.SalaryDistributionReportDto;
import com.example.employee_management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/monthly-hiring")
    public ResponseEntity<List<MonthlyHiringReportDto>> getMonthlyHiringReport() {
        return ResponseEntity.ok(employeeService.getMonthlyHiringReport());
    }

    @GetMapping("/salary-distribution")
    public ResponseEntity<List<SalaryDistributionReportDto>> getSalaryDistributionReport() {
        return ResponseEntity.ok(employeeService.getSalaryDistributionReport());
    }

    @GetMapping("/department-employee-count")
    public ResponseEntity<List<DepartmentEmployeeCountDto>> getDepartmentEmployeeCountReport() {
        return ResponseEntity.ok(employeeService.getDepartmentEmployeeCountReport());
    }
}

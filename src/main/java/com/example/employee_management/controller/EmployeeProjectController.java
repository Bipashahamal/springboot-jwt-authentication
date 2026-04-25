package com.example.employee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.employee_management.entity.EmployeeProject;
import com.example.employee_management.service.EmployeeProjectService;

@RestController
@RequestMapping("/api/employee-project")
public class EmployeeProjectController {

    @Autowired
    private EmployeeProjectService service;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PROJECT')")
    public EmployeeProject assign(
            @RequestParam Long employeeId,
            @RequestParam Long projectId,
            @RequestParam String role) {

        return service.assignProject(employeeId, projectId, role);
    }
} 
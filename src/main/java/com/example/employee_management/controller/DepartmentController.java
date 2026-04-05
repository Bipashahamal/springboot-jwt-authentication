package com.example.employee_management.controller;

import com.example.employee_management.entity.Department;
import com.example.employee_management.dto.DepartmentRequest;
import com.example.employee_management.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Create Department
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    // Update Department
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id,
            @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    // Get all Departments
    @GetMapping
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    // Assign Employees to Department
    @PostMapping("/{id}/employees")
    public ResponseEntity<Department> assignEmployees(@PathVariable Long id,
            @RequestBody List<Long> employeeIds) {
        return ResponseEntity.ok(departmentService.assignEmployees(id, employeeIds));
    }
}
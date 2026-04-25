package com.example.employee_management.controller;

import com.example.employee_management.entity.Department;
import com.example.employee_management.dto.DepartmentRequest;
import com.example.employee_management.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Create Department
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_DEPARTMENT')")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequest request) {
        try {
            return ResponseEntity.ok(departmentService.createDepartment(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update Department
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_DEPARTMENT')")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id,
            @RequestBody DepartmentRequest request) {
        try {
            return ResponseEntity.ok(departmentService.updateDepartment(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all Departments
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_DEPARTMENT')")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
}
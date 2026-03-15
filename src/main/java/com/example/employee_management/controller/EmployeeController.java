package com.example.employee_management.controller;

import com.example.employee_management.dto.EmployeeRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ✅ CREATE EMPLOYEE (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequest request) {

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    // ✅ GET ALL (ADMIN & USER)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(employeeService.getAllEmployees(page, size, sortBy));
    }

    // ✅ GET MY PROFILE
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmail(authentication.getName()));
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ✅ UPDATE
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @employeeService.isOwner(authentication.name, #id))")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .build();

        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    // ✅ DELETE
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @employeeService.isOwner(authentication.name, #id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {
        Employee deletedEmployee = employeeService.deleteEmployee(id);

        if (deletedEmployee.getEmail().equalsIgnoreCase(authentication.getName())) {
            return ResponseEntity.ok("You deleted your data successfully");
        } else {
            return ResponseEntity.ok("Employee deleted successfully");
        }
    }
}
package com.example.employee_management.controller;

import com.example.employee_management.dto.EmployeeRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;
import com.example.employee_management.service.FileService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private FileService fileService;

    // ✅ CREATE EMPLOYEE (ADMIN only)
    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Map<String, String>> createEmployee(@Valid @RequestBody EmployeeRequest request) {

        employeeService.createEmployee(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee created successfully");
        response.put("emailStatus", "Welcome email will be sent in the background");

        return ResponseEntity.ok(response);
    }

    // ✅ GET ALL (ADMIN & USER)
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping({"", "/search"})
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary) {

        return ResponseEntity.ok(employeeService.getAllEmployees(page, size, sortBy, name, email, department, minSalary, maxSalary));
    }

    // ✅ GET ALL (NON-PAGINATED)
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllEmployeesList() {
        return ResponseEntity.ok(employeeService.getAllEmployeesList());
    }

    // ✅ GET MY PROFILE
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/me")
    public ResponseEntity<Employee> getMyProfile(
            org.springframework.security.core.Authentication authentication) {

        return ResponseEntity.ok(employeeService.getEmployeeByEmail(authentication.getName()));
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {

        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ✅ UPDATE EMPLOYEE
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE') or (hasAuthority('VIEW_EMPLOYEE') and @employeeService.isOwner(authentication.name, #id))")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<Employee> restoreEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.restoreEmployee(id));
    }

    // ✅ DELETE EMPLOYEE
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE') or (hasAuthority('VIEW_EMPLOYEE') and @employeeService.isOwner(authentication.name, #id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        Employee deletedEmployee = employeeService.deleteEmployee(id);

        if (deletedEmployee.getEmail().equalsIgnoreCase(authentication.getName())) {
            return ResponseEntity.ok("You deleted your data successfully");
        } else {
            return ResponseEntity.ok("Employee deleted successfully");
        }

    }

    // ✅ UPLOAD PROFILE IMAGE
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE') or (hasAuthority('VIEW_EMPLOYEE') and @employeeService.isOwner(authentication.name, #id))")
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @PathVariable Long id, 
            @RequestParam("file") MultipartFile file) {

        String fileName = fileService.uploadFile(file);
        employeeService.updateProfileImage(id, fileName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile image uploaded successfully");
        response.put("profileImage", fileName);

        return ResponseEntity.ok(response);
    }



}
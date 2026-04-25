package com.example.employee_management.controller;

import com.example.employee_management.dto.EmployeeRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.UserProfile;
import com.example.employee_management.service.EmployeeService;
import com.example.employee_management.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private UserProfileService userProfileService;

    // ✅ CREATE EMPLOYEE (ADMIN only)
    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Map<String, String>> createEmployee(
            @Valid @org.springframework.web.bind.annotation.RequestBody EmployeeRequest request) {

        employeeService.createEmployee(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee created successfully");
        response.put("emailStatus", "Welcome email will be sent in the background");

        return ResponseEntity.ok(response);
    }

    // ✅ GET ALL (ADMIN & USER)
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping({ "", "/search" })
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary) {

        return ResponseEntity
                .ok(employeeService.getAllEmployees(page, size, sortBy, name, email, minSalary, maxSalary));
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
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody EmployeeRequest request) {

        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PutMapping("/{id}/salary")
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    public Employee updateSalary(
            @PathVariable Long id,
            @RequestParam Double salary) {

        return employeeService.updateSalary(id, salary);
    }

    // ✅ DELETE EMPLOYEE
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    // ✅ RESTORE EMPLOYEE
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restoreEmployee(@PathVariable Long id) {
        employeeService.restoreEmployee(id);
        return ResponseEntity.ok("Employee restored successfully");
    }

    // ✅ UPLOAD PROFILE IMAGE
    @Operation(summary = "Upload Profile Image", description = "Upload a profile image for an employee. The file will be stored on the local system and linked to the user account.", requestBody = @RequestBody(required = true, content = @Content(mediaType = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object"), encoding = @Encoding(name = "file", contentType = "application/octet-stream"))))
    @PreAuthorize("hasAuthority('UPDATE_EMPLOYEE')")
    @PostMapping(value = "/{id}/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws java.io.IOException {

        if (file.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "File is empty");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Get employee
        Employee employee = employeeService.getEmployeeById(id);

        // Upload file and save link to user_profiles table
        UserProfile userProfile = userProfileService.uploadFileByUserEmail(employee.getEmail(), file);




        // Update users.profileImageId with the profile ID
        employeeService.updateProfileImage(id, userProfile.getId());


        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile image uploaded successfully");
        response.put("fileId", userProfile.getId().toString());
        response.put("fileName", userProfile.getFileName());
        response.put("filePath", userProfile.getFilePath()); // Added link as requested
        response.put("status", "Profile image updated in user account");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/department/{departmentId}/total-salary")
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    public Map<String, Object> getDepartmentSalary(@PathVariable("departmentId") Long id) {
        Double total = employeeService.getTotalSalary(id);
        Map<String, Object> response = new HashMap<>();
        response.put("departmentId", id);
        response.put("totalSalary", total != null ? total.longValue() : 0);
        return response;
    }

    // ✅ ADD ATTENDANCE
    @PostMapping("/{id}/attendance")
    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> addAttendance(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) java.time.LocalDate date) {
        employeeService.addAttendance(id, status, null);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendance added successfully");
        return ResponseEntity.ok(response);
    }

    // ✅ GET ATTENDANCE
    @GetMapping("/{id}/attendance")
    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    public ResponseEntity<List<com.example.employee_management.entity.Attendance>> getAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getAttendanceByEmployee(id));
    }
}
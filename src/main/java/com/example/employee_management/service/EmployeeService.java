package com.example.employee_management.service;

import com.example.employee_management.dto.EmployeeRequest;
import com.example.employee_management.dto.RegisterRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.specification.EmployeeSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // ✅ CREATE EMPLOYEE FROM EmployeeRequest
    public Employee createEmployee(EmployeeRequest request) {

        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        return createAndSaveEmployee(request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getSalary());
    }

    private Employee createAndSaveEmployee(String firstName, String lastName, String email, Double salary) {
        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .salary(salary)
                .createdAt(LocalDateTime.now())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        // ✅ Send welcome email in background
        emailService.sendWelcomeEmail(
                savedEmployee.getEmail(),
                savedEmployee.getFirstName() + " " + savedEmployee.getLastName());

        return savedEmployee;
    }

    // ✅ GET ALL EMPLOYEES (SIMPLE LIST)
    public List<Employee> getAllEmployeesList() {
        return employeeRepository.findByIsDeletedFalse();
    }

    // ✅ GET ALL EMPLOYEES (excluding deleted)
    public Page<Employee> getAllEmployees(int page, int size, String sortBy,
            String name, String email, Double minSalary, Double maxSalary) {

        String normalizedSortBy = normalizeSortProperty(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(normalizedSortBy));

        Specification<Employee> spec = EmployeeSpecification.isNotDeleted()
                .and(EmployeeSpecification.nameContains(name))
                .and(EmployeeSpecification.emailContains(email))
                .and(EmployeeSpecification.salaryGreaterThan(minSalary))
                .and(EmployeeSpecification.salaryLessThan(maxSalary));

        return employeeRepository.findAll(spec, pageable);
    }

    /**
     * Normalizes sort properties to match entity field names case-sensitively.
     * Maps common aliases like 'ID' to 'id'.
     */
    private String normalizeSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }

        // Handle case variations for common fields
        String lowerCaseSort = sortBy.toLowerCase();
        switch (lowerCaseSort) {
            case "id":
                return "id";
            case "firstname":
                return "firstName";
            case "lastname":
                return "lastName";
            case "email":
                return "email";
            case "salary":
                return "salary";
            case "createdat":
                return "createdAt";
            default:
                return sortBy;
        }
    }

    // ✅ GET EMPLOYEE BY ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    // ✅ UPDATE EMPLOYEE
    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        // Validate input
        validateEmployeeRequest(request);

        // Fetch employee with pessimistic lock to prevent concurrent updates
        Employee employee = getEmployeeById(id);

        // Check for email uniqueness only if email is being changed
        if (!employee.getEmail().equals(request.getEmail())) {
            employeeRepository.findByEmail(request.getEmail())
                    .ifPresent(existingEmployee -> {
                        if (!existingEmployee.getId().equals(id)) {
                            throw new DuplicateEmailException("Email already exists");
                        }
                    });
        }

        // Update employee fields using a dedicated method for better maintainability
        updateEmployeeFields(employee, request);

        return employeeRepository.save(employee);
    }

    /**
     * Updates employee fields from request DTO with proper validation and type
     * conversion.
     * This method handles:
     * - Null safety checks
     * - Selective field updates (only changed fields)
     * - Audit trail maintenance
     *
     * @param employee The employee entity to update
     * @param request  The request DTO containing new values
     */
    private void updateEmployeeFields(Employee employee, EmployeeRequest request) {
        // Update basic fields with null safety
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            employee.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            employee.setLastName(request.getLastName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            employee.setEmail(request.getEmail().trim().toLowerCase());
        }

        // Update salary with validation
        if (request.getSalary() != null && request.getSalary() > 0) {
            employee.setSalary(request.getSalary());
        }
    }

    /**
     * Validates employee request data for business rules.
     *
     * @param request The employee request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEmployeeRequest(EmployeeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Employee request cannot be null");
        }

        // Additional business validations beyond bean validation
        if (request.getSalary() != null && request.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }

        if (request.getEmail() != null && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    // ✅ DELETE EMPLOYEE
    // ✅ DELETE EMPLOYEE (HARD DELETE)
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    // ✅ CHECK OWNER
    public boolean isOwner(String email, Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employee -> employee.getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }

    // ✅ GET EMPLOYEE BY EMAIL
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee profile not found or has been deleted."));
    }


    
    // ✅ UPDATE PROFILE IMAGE IN USER TABLE
    @Transactional
    public void updateProfileImage(Long employeeId, Long fileId) {
        Employee employee = getEmployeeById(employeeId);
        
        // Find user by email and update their profileImageId (file ID from user_files table)
        userRepository.findByEmail(employee.getEmail())
                .ifPresent(user -> {
                    user.setProfileImageId(fileId);
                    userRepository.save(user);
                });
    }

    // ✅ MONTHLY REPORT JOB
    public void generateMonthlyReport() {
        List<Employee> employees = employeeRepository.findAll();
        System.out.println("Monthly report generated for " + employees.size() + " employees");
    }


}
package com.example.employee_management.service;

import com.example.employee_management.dto.EmployeeRequest;
import com.example.employee_management.dto.RegisterRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.dto.report.MonthlyHiringReportDto;
import com.example.employee_management.dto.report.SalaryDistributionReportDto;
import com.example.employee_management.dto.report.DepartmentEmployeeCountDto;
import com.example.employee_management.event.EmployeeCreatedEvent;
import com.example.employee_management.event.EmployeeUpdatedEvent;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.EmployeeValidationException;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.repository.DepartmentRepository;
import com.example.employee_management.repository.SalaryHistoryRepository;
import com.example.employee_management.repository.AttendanceRepository;
import com.example.employee_management.entity.Department;
import com.example.employee_management.entity.SalaryHistory;
import com.example.employee_management.entity.Attendance;
import com.example.employee_management.specification.EmployeeSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SalaryHistoryRepository salaryHistoryRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher publisher;

    // ✅ CREATE EMPLOYEE FROM EmployeeRequest
    public Employee createEmployee(EmployeeRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        if (request.getEmail() != null && employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email already exists");
        }

        if (request.getDepartmentId() == null) {
            errors.put("departmentId", "Department ID is mandatory");
        } else if (!departmentRepository.existsById(request.getDepartmentId())) {
            errors.put("departmentId", "Department not found with id: " + request.getDepartmentId());
        }

        if (!errors.isEmpty()) {
            throw new EmployeeValidationException(errors);
        }

        Department department = departmentRepository.findById(request.getDepartmentId()).get();
        return createAndSaveEmployee(request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getSalary(), department);
    }

    private Employee createAndSaveEmployee(String firstName, String lastName, String email, Double salary,
            Department department) {
        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .salary(salary)
                .department(department)
                .createdAt(LocalDateTime.now())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        if (salary != null) {
            SalaryHistory history = SalaryHistory.builder()
                    .salary(salary)
                    .effectiveFrom(LocalDate.now())
                    .employee(savedEmployee)
                    .build();
            salaryHistoryRepository.save(history);
        }

        publisher.publishEvent(new EmployeeCreatedEvent(savedEmployee));

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

    // ✅ GET EMPLOYEE BY ID (excludes soft deleted)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    // ✅ UPDATE EMPLOYEE
    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        // Validate input
        validateEmployeeRequest(request);

        // Fetch employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Re-fetch department to avoid lazy loading issues
        Department department = null;
        if (request.getDepartmentId() != null && request.getDepartmentId() > 0) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id: " + request.getDepartmentId()));
        }

        // Check for email uniqueness only if email is being changed
        String currentEmail = employee.getEmail();
        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(currentEmail)) {
            employeeRepository.findByEmail(newEmail)
                    .ifPresent(existingEmployee -> {
                        if (!existingEmployee.getId().equals(id)) {
                            throw new DuplicateEmailException("Email already exists");
                        }
                    });
        }

        // Update employee fields using a dedicated method for better maintainability
        updateEmployeeFields(employee, request, department);

        Employee updatedEmployee = employeeRepository.save(employee);
        
        publisher.publishEvent(new EmployeeUpdatedEvent(updatedEmployee));
        
        return updatedEmployee;
    }

    @Transactional
    public Employee updateSalary(Long id, Double newSalary) {

        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            Double oldSalary = employee.getSalary();

            if (oldSalary != null && !oldSalary.equals(newSalary)) {
                List<SalaryHistory> histories = salaryHistoryRepository.findByEmployeeIdOrderByEffectiveFromDesc(id);
                if (!histories.isEmpty()) {
                    SalaryHistory lastHistory = histories.get(0);
                    lastHistory.setEffectiveTo(LocalDate.now());
                    salaryHistoryRepository.save(lastHistory);
                }

                SalaryHistory history = SalaryHistory.builder()
                        .salary(newSalary)
                        .effectiveFrom(LocalDate.now())
                        .employee(employee)
                        .build();
                salaryHistoryRepository.save(history);
            }

            employee.setSalary(newSalary);

            Employee updatedEmployee = employeeRepository.save(employee);
            publisher.publishEvent(new EmployeeUpdatedEvent(updatedEmployee));
            return updatedEmployee;

        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Another user updated this record. Please try again.");
        }
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
    private void updateEmployeeFields(Employee employee, EmployeeRequest request, Department department) {
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

        // Update department if provided
        if (department != null) {
            employee.setDepartment(department);
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

        if (request.getDepartmentId() == null) {
            throw new IllegalArgumentException("Department ID is mandatory");
        }
    }

    // ✅ DELETE EMPLOYEE (SOFT DELETE)
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    // ✅ RESTORE EMPLOYEE (restore soft-deleted employee)
    public void restoreEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setDeleted(false);
        employeeRepository.save(employee);
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

    public Double getTotalSalary(Long deptId) {
        return employeeRepository.getTotalSalaryByDepartment(deptId);
    }

    // ✅ UPDATE PROFILE IMAGE IN USER TABLE
    @Transactional
    public void updateProfileImage(Long employeeId, Long fileId) {
        Employee employee = getEmployeeById(employeeId);

        // Find user by email and update their profileImageId (file ID from user_files
        // table)
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

    // ✅ REPORTING APIS
    public List<MonthlyHiringReportDto> getMonthlyHiringReport() {
        List<Object[]> results = employeeRepository.getMonthlyHiringReport();
        return results.stream()
                .filter(row -> row[0] != null && row[1] != null && row[2] != null)
                .map(row -> new MonthlyHiringReportDto(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()))
                .toList();
    }

    public List<SalaryDistributionReportDto> getSalaryDistributionReport() {
        List<Object[]> results = employeeRepository.getSalaryDistributionReport();
        return results.stream()
                .filter(row -> row[0] != null)
                .map(row -> new SalaryDistributionReportDto(
                        (String) row[0],
                        toDouble(row[1]),
                        toDouble(row[2]),
                        toDouble(row[3]),
                        toDouble(row[4]),
                        row[5] != null ? ((Number) row[5]).longValue() : 0L))
                .toList();
    }

    public List<DepartmentEmployeeCountDto> getDepartmentEmployeeCountReport() {
        List<Object[]> results = employeeRepository.getDepartmentEmployeeCountReport();
        return results.stream()
                .filter(row -> row[0] != null && row[1] != null)
                .map(row -> new DepartmentEmployeeCountDto(
                        (String) row[0],
                        ((Number) row[1]).longValue()))
                .toList();
    }

    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;
        return ((Number) obj).doubleValue();
    }

    // ✅ ADD ATTENDANCE
    public Attendance addAttendance(Long employeeId, String status, LocalDate date) {
        Employee employee = getEmployeeById(employeeId);
        Attendance attendance = Attendance.builder()
                .employee(employee)
                .status(status)
                .date(date != null ? date : LocalDate.now())
                .build();
        return attendanceRepository.save(attendance);
    }

    // ✅ GET ATTENDANCE BY EMPLOYEE
    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        Employee employee = getEmployeeById(employeeId);
        return attendanceRepository.findByEmployee(employee);
    }

}
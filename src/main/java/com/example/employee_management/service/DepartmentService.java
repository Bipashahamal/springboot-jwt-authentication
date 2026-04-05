package com.example.employee_management.service;

import com.example.employee_management.entity.Department;
import com.example.employee_management.dto.DepartmentRequest;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.exception.ResourceNotFoundException;
import com.example.employee_management.repository.DepartmentRepository;
import com.example.employee_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create department
    public Department createDepartment(DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return departmentRepository.save(department);
    }

    // Update department
    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        return departmentRepository.save(department);
    }

    // Get all departments
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Assign employee to department
    @Transactional
    public Department assignEmployees(Long departmentId, List<Long> employeeIds) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        employees.forEach(emp -> emp.setDepartment(department));
        employeeRepository.saveAll(employees);

        // Reload the department to get the fresh list of employees
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }
}
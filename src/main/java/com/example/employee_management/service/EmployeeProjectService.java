package com.example.employee_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.employee_management.entity.EmployeeProject;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.Project;
import com.example.employee_management.repository.EmployeeProjectRepository;
import com.example.employee_management.repository.EmployeeRepository;
import com.example.employee_management.repository.ProjectRepository;

@Service
public class EmployeeProjectService {

    @Autowired
    private EmployeeProjectRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public EmployeeProject assignProject(Long empId, Long projectId, String role) {

        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        EmployeeProject ep = EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .role(role)
                .build();

        return repository.save(ep);
    }
}
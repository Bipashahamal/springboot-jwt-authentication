package com.example.employee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.employee_management.entity.Project;
import com.example.employee_management.service.ProjectService;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @PostMapping
    public Project create(@RequestBody Project project) {
        return service.createProject(project);
    }

    @GetMapping
    public List<Project> getAll() {
        return service.getAllProjects();
    }
}
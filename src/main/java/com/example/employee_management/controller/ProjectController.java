package com.example.employee_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.employee_management.entity.Project;
import com.example.employee_management.service.ProjectService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PROJECT')")
    public ResponseEntity<?> create(@RequestBody Project project) {
        try {
            return ResponseEntity.ok(service.createProject(project));
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", message));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Project with name '" + project.getName() + "' already exists"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PROJECT')")
    public List<Project> getAll() {
        return service.getAllProjects();
    }
}
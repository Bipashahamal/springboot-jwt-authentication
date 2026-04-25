package com.example.employee_management.service;

import com.example.employee_management.entity.Project;
import com.example.employee_management.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(Project project) {
        if (projectRepository.existsByName(project.getName())) {
            throw new RuntimeException("Project with name '" + project.getName() + "' already exists");
        }
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
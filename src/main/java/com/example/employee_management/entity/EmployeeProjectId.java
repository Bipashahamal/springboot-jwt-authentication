package com.example.employee_management.entity;

import java.io.Serializable;
import java.util.Objects;

public class EmployeeProjectId implements Serializable {

    private Long employee;
    private Long project;

    public EmployeeProjectId() {}

    public EmployeeProjectId(Long employee, Long project) {
        this.employee = employee;
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeProjectId that = (EmployeeProjectId) o;
        return Objects.equals(employee, that.employee) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, project);
    }
}

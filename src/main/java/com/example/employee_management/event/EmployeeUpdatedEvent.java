package com.example.employee_management.event;

import com.example.employee_management.entity.Employee;

public class EmployeeUpdatedEvent {

    private final Employee employee;

    public EmployeeUpdatedEvent(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }
}

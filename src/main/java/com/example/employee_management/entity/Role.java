package com.example.employee_management.entity;

import java.util.Set;

public enum Role {

    SYSTEM_ADMIN(Set.of(
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.DELETE_EMPLOYEE,
            Permission.VIEW_EMPLOYEE)),

    USER_ADMIN(Set.of(
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.VIEW_EMPLOYEE)),

    EMPLOYEE_VIEWER(Set.of(
            Permission.VIEW_EMPLOYEE)),

    // Added to support legacy database records
    USER(Set.of(
            Permission.VIEW_EMPLOYEE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
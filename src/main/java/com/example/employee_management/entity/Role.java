package com.example.employee_management.entity;

import java.util.Set;

public enum Role {

    SYSTEM_ADMIN(Set.of(
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.DELETE_EMPLOYEE,
            Permission.VIEW_EMPLOYEE,
            Permission.CREATE_DEPARTMENT,
            Permission.UPDATE_DEPARTMENT,
            Permission.VIEW_DEPARTMENT,
            Permission.CREATE_PROJECT,
            Permission.UPDATE_PROJECT,
            Permission.VIEW_PROJECT)),

    USER_ADMIN(Set.of(
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.VIEW_EMPLOYEE,
            Permission.CREATE_DEPARTMENT,
            Permission.UPDATE_DEPARTMENT,
            Permission.VIEW_DEPARTMENT,
            Permission.CREATE_PROJECT,
            Permission.VIEW_PROJECT)),

    // EMPLOYEE_VIEWER: Can only view employees and reports
    EMPLOYEE_VIEWER(Set.of(
            Permission.VIEW_EMPLOYEE,
            Permission.VIEW_DEPARTMENT,
            Permission.VIEW_PROJECT)),

    // USER: Employee with limited scope - can upload own profile/documents
    USER(Set.of(
            Permission.VIEW_EMPLOYEE,
            Permission.VIEW_DEPARTMENT,
            Permission.VIEW_PROJECT,
            Permission.UPLOAD_OWN_PROFILE,
            Permission.UPLOAD_DOCUMENT));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
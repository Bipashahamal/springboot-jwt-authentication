package com.example.employee_management.entity;

public enum Permission {

    // Employee permissions
    CREATE_EMPLOYEE,
    UPDATE_EMPLOYEE,
    DELETE_EMPLOYEE,
    VIEW_EMPLOYEE,

    // Department permissions
    CREATE_DEPARTMENT,
    UPDATE_DEPARTMENT,
    VIEW_DEPARTMENT,

    // Project permissions
    CREATE_PROJECT,
    UPDATE_PROJECT,
    VIEW_PROJECT,

    // File/Profile permissions
    UPLOAD_OWN_PROFILE,
    UPLOAD_DOCUMENT
}
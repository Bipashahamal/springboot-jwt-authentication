package com.example.employee_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;         // ID of the user performing the action
    private String action;       // CREATE, UPDATE, DELETE
    private String entity;       // e.g., Employee, Department
    private LocalDateTime timestamp;
}
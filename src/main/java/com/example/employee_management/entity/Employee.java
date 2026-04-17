package com.example.employee_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private Double salary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT(1) DEFAULT 0")
    private boolean isDeleted = false;

    private Long profileImageId;  // ← Store file ID from user_files table

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
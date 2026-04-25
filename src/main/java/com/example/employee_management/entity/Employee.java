package com.example.employee_management.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.example.employee_management.entity.EmployeeProject;

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
@Column(nullable = false)

    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private Double salary;

//    @Version
//    private Long version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private List<EmployeeProject> employeeProjects;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private List<SalaryHistory> salaryHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private List<Attendance> attendances;
    
    @Builder.Default
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT(1) DEFAULT 0")
    private boolean isDeleted = false;


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
package com.example.employee_management.repository;

import com.example.employee_management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List; // Added this import as it's used later in the file
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmailAndIsDeletedFalse(String email);

    Page<Employee> findByIsDeletedFalse(Pageable pageable);

    List<Employee> findByIsDeletedFalse();

    @Query(value = "SELECT get_total_salary_by_department(:deptId)", nativeQuery = true)
    Double getTotalSalaryByDepartment(@Param("deptId") Long deptId);
}
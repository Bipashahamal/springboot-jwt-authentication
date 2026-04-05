package com.example.employee_management.repository;

import com.example.employee_management.entity.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @EntityGraph(attributePaths = {"employees"})
    List<Department> findAll();

    @EntityGraph(attributePaths = {"employees"})
    Optional<Department> findById(Long id);

    Optional<Department> findByName(String name);
    Optional<Department> findByNameIgnoreCase(String name);

    @Query("SELECT d FROM Department d WHERE " +
           "LOWER(TRIM(d.name)) = LOWER(:name) OR " +
           "LOWER(TRIM(d.description)) = LOWER(:name)")
    Optional<Department> findByNameOrDescriptionIgnoreCase(@Param("name") String name);
}
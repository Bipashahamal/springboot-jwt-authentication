package com.example.employee_management.repository;

import com.example.employee_management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.employee_management.dto.report.MonthlyHiringReportDto;
import com.example.employee_management.dto.report.SalaryDistributionReportDto;
import com.example.employee_management.dto.report.DepartmentEmployeeCountDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmailAndIsDeletedFalse(String email);

    Page<Employee> findByIsDeletedFalse(Pageable pageable);

    List<Employee> findByIsDeletedFalse();

    @Query(value = "SELECT get_total_salary_by_department(:deptId)", nativeQuery = true)
    Double getTotalSalaryByDepartment(@Param("deptId") Long deptId);

    @Query(value = "SELECT YEAR(created_at) as hiring_year, MONTH(created_at) as hiring_month, COUNT(*) as employee_count " +
           "FROM employee WHERE is_deleted = false " +
           "GROUP BY YEAR(created_at), MONTH(created_at) " +
           "ORDER BY YEAR(created_at) DESC, MONTH(created_at) DESC", nativeQuery = true)
    List<Object[]> getMonthlyHiringReport();

    @Query(value = "SELECT d.name as department_name, SUM(e.salary) as total_salary, AVG(e.salary) as avg_salary, MAX(e.salary) as max_salary, MIN(e.salary) as min_salary, COUNT(e.id) as employee_count " +
           "FROM employee e JOIN department d ON e.department_id = d.id " +
           "WHERE e.is_deleted = false " +
           "GROUP BY d.name ORDER BY d.name ASC", nativeQuery = true)
    List<Object[]> getSalaryDistributionReport();

    @Query(value = "SELECT d.name as department_name, COUNT(e.id) as employee_count " +
           "FROM employee e JOIN department d ON e.department_id = d.id " +
           "WHERE e.is_deleted = false " +
           "GROUP BY d.name ORDER BY employee_count DESC", nativeQuery = true)
    List<Object[]> getDepartmentEmployeeCountReport();
}
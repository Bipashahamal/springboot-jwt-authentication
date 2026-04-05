package com.example.employee_management.specification;

import com.example.employee_management.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("isDeleted"), false);
    }

    /**
     * Search by name (checks both firstName and lastName)
     */
    public static Specification<Employee> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            String pattern = "%" + name.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern));
        };
    }

    public static Specification<Employee> emailContains(String email) {
        return (root, query, cb) -> (email == null || email.trim().isEmpty()) ? null
                : cb.like(cb.lower(root.get("email")), "%" + email.trim().toLowerCase() + "%");
    }

    public static Specification<Employee> departmentEquals(String department) {
        return (root, query, cb) -> (department == null || department.trim().isEmpty()) ? null
                : cb.equal(cb.lower(root.get("department").get("name")), department.trim().toLowerCase());
    }

    public static Specification<Employee> salaryGreaterThan(Double minSalary) {
        return (root, query, cb) -> minSalary == null ? null : cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
    }

    public static Specification<Employee> salaryLessThan(Double maxSalary) {
        return (root, query, cb) -> maxSalary == null ? null : cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
    }
}
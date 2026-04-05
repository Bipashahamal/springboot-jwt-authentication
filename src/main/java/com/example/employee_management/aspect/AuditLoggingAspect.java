package com.example.employee_management.aspect;

import com.example.employee_management.service.AuditLogService;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Aspect
@Component
public class AuditLoggingAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    // Intercept all create/update/delete methods in EmployeeService
    @After("execution(* com.example.employee_management.service.EmployeeService.createEmployee(..))")
    public void logCreateEmployee(JoinPoint joinPoint) {
        logAction("CREATE", "Employee");
    }

    @After("execution(* com.example.employee_management.service.EmployeeService.updateEmployee(..))")
    public void logUpdateEmployee(JoinPoint joinPoint) {
        logAction("UPDATE", "Employee");
    }

    @After("execution(* com.example.employee_management.service.EmployeeService.deleteEmployee(..))")
    public void logDeleteEmployee(JoinPoint joinPoint) {
        logAction("DELETE", "Employee");
    }

    private void logAction(String action, String entity) {
        Long userId = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                userId = userOptional.get().getId();
            }
        }
        
        // Fallback to a system/admin ID if no authenticated user can be resolved
        if (userId == null) {
            userId = 1L;
        }

        auditLogService.logAction(userId, action, entity);
    }
}
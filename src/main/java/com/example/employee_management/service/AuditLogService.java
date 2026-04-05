package com.example.employee_management.service;

import com.example.employee_management.entity.AuditLog;
import com.example.employee_management.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(Long userId, String action, String entity) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entity(entity)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}
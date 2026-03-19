package com.example.employee_management.repository;

import com.example.employee_management.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
     Optional<User> findByPhoneNumber(String phoneNumber);
}
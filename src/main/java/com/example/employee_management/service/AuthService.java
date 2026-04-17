package com.example.employee_management.service;

import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.RegisterRequest;
import com.example.employee_management.entity.RefreshToken;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.exception.RegistrationException;
import com.example.employee_management.exception.InvalidCredentialsException;
import com.example.employee_management.repository.RefreshTokenRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // ================= REGISTER =================
    public ResponseEntity<Map<String, String>> register(RegisterRequest request) {

        // Collect errors
        Map<String, String> errors = new LinkedHashMap<>();

        // Check for duplicate email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email already exists!");
        }

        // Check for duplicate phone number
        if (request.getPhoneNumber() != null &&
                userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            errors.put("phoneNumber", "Phone number already exists!");
        }

        // Determine Role
        Role userRole = Role.EMPLOYEE_VIEWER;
        if (request.getRole() != null) {
            try {
                userRole = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.put("role", "Invalid role: " + request.getRole() + ". Valid roles are: SYSTEM_ADMIN, USER_ADMIN, EMPLOYEE_VIEWER");
            }
        }

        // Validate employeeId based on role
        if (!errors.containsKey("role")) {
            if (userRole == Role.SYSTEM_ADMIN && request.getEmployeeId() != null) {
                errors.put("employeeId", "employeeId must not be provided for SYSTEM_ADMIN");
            } else if (userRole != Role.SYSTEM_ADMIN && request.getEmployeeId() == null) {
                errors.put("employeeId", "employeeId is mandatory for role: " + userRole.name());
            }
        }

        // If there are errors, throw RegistrationException
        if (!errors.isEmpty()) {
            throw new RegistrationException(errors);
        }

        // Create User
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(userRole);

        // Link to existing employee if employeeId is provided
        if (request.getEmployeeId() != null) {
            // Verify employee existence
            employeeService.getEmployeeById(request.getEmployeeId());
            user.setEmployeeId(request.getEmployeeId());
        }

        userRepository.save(user);

        // Response
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "User registered successfully!");
        response.put("emailStatus", "Welcome email will be sent in the background");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= LOGIN =================
    public Map<String, Object> login(LoginRequest request) {

        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Authenticate user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Generate Refresh token
        RefreshToken refreshToken = createRefreshToken(user.getEmail());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken.getToken());
        response.put("tokenType", "Bearer");
        response.put("accesstokenexpiresIn", jwtUtil.getExpirationTime());

        return response;
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .username(username)
                .expiryDate(Instant.now().plusSeconds(86400))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public String refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired. Please log in again");
        }
        return jwtUtil.generateToken(refreshToken.getUsername());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(refreshTokenRepository::delete);
    }

    public long getExpirationTime() {
        return jwtUtil.getExpirationTime();
    }

    public String getFormattedExpirationTime() {
        return jwtUtil.getExpirationTimeFormatted();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }
}

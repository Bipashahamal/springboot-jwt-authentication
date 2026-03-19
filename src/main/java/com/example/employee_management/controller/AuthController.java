package com.example.employee_management.controller;

import com.example.employee_management.dto.LoginRequest;
import com.example.employee_management.dto.RegisterRequest;
import com.example.employee_management.entity.*;
import com.example.employee_management.service.EmailService;
import com.example.employee_management.service.EmployeeService;
import com.example.employee_management.exception.DuplicateEmailException;
import com.example.employee_management.exception.InvalidCredentialsException;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.util.JwtUtil;

import java.util.HashMap;
import com.example.employee_management.exception.DuplicatePhoneNumberException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmailService emailService;

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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists!");
        }

          // ✅ Check phone number duplicate
    if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
        throw new DuplicatePhoneNumberException("Phone number already exists!");
    }


        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        }

        userRepository.save(user);

        // ✅ Automatically create Employee profile, unless one already exists
        try {
            String[] nameParts = request.getName().split(" ", 2);
            Employee employee = Employee.builder()
                    .firstName(nameParts[0])
                    .lastName(nameParts.length > 1 ? nameParts[1] : "")
                    .email(request.getEmail())
                    .department("To be defined")
                    .salary(0.0)
                    .build();
            employeeService.createEmployee(employee);
           
        } catch (DuplicateEmailException e) {
            // An admin already created this employee profile. That's perfectly fine!
            // We just ignore the duplicate error so the User can still register
            // successfully.
            System.out.println(
                    "Employee profile already exists for: " + request.getEmail() + ". Linking to existing profile.");
        }

        // 3️⃣ Send welcome email asynchronously for user
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        // ✅ Return response including email info
    Map<String, String> response = new HashMap<>();
    response.put("message", "User registered successfully!");
    response.put("emailStatus", "Welcome email will be sent in the background");

        return new ResponseEntity<Map<String, String>>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        // 1. First check if the user even exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // 2. Then check if the password matches
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("tokenType", "Bearer");
        return response;
    }
}
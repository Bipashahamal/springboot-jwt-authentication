package com.example.employee_management.security;

import com.example.employee_management.entity.User;
import com.example.employee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Build authorities set
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Add permissions from role
        user.getRole().getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.name()))
        );

        // Return Spring Security User
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
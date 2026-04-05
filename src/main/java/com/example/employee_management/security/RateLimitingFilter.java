package com.example.employee_management.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private static final long TIME_WINDOW = 60 * 1000; // 1 minute

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        String user = getUserIdentifier();
        int maxRequests = getLimitByRole(); // 🔥 role-based limit

        long currentTime = Instant.now().toEpochMilli();

        requestCounts.putIfAbsent(user, new RequestInfo(0, currentTime));

        RequestInfo info = requestCounts.get(user);

        // Reset after time window
        if (currentTime - info.startTime > TIME_WINDOW) {
            info.count = 0;
            info.startTime = currentTime;
        }

        info.count++;

        if (info.count > maxRequests) {
            res.setStatus(429);
            res.getWriter().write("Rate limit exceeded for your role");
            return;
        }

        chain.doFilter(request, response);
    }

    // 🔥 ROLE-BASED LIMIT LOGIC
    private int getLimitByRole() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return 50; // anonymous users
        }

        for (GrantedAuthority authority : auth.getAuthorities()) {

            if (authority.getAuthority().equals("ROLE_SYSTEM_ADMIN")) {
                return 300; // highest limit
            }

            if (authority.getAuthority().equals("ROLE_USER_ADMIN")) {
                return 200;
            }

            if (authority.getAuthority().equals("ROLE_EMPLOYEE_VIEWER")) {
                return 100;
            }
        }

        return 50; // default fallback
    }

    // 🔑 Identify user (email from JWT)
    private String getUserIdentifier() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "anonymous";
        }
    }

    // Helper class
    static class RequestInfo {
        int count;
        long startTime;

        RequestInfo(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}
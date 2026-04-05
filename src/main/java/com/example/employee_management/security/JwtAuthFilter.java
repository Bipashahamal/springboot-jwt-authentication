package com.example.employee_management.security;

import com.example.employee_management.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
                String path = request.getRequestURI();

if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
    filterChain.doFilter(request, response);
    return;
}

        // 🔥 Skip login/register and public endpoints
        if (request.getServletPath().startsWith("/api/auth") || request.getServletPath().startsWith("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"error\": \"Token Required\", \"message\": \"Token is required\", \"status\": 401}");
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String email = jwtUtil.extractEmail(token);

                if (email != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    var userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtUtil.validateToken(token)) {

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request));

                        SecurityContextHolder.getContext()
                                .setAuthentication(authToken);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                System.out.println("ExpiredJwtException: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter()
                        .write("{\"error\": \"JWT EXPIRE\", \"message\": \"JWT token has expired\", \"status\": 401}");
                return;
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                System.out.println("UsernameNotFoundException: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter()
                        .write("{\"error\": \"Unauthorized\", \"message\": \"User not found\", \"status\": 401}");
                return;
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter()
                        .write("{\"error\": \"Unauthorized\", \"message\": \"Invalid JWT token\", \"status\": 401}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
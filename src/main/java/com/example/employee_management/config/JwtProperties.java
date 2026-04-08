package com.example.employee_management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key for JWT token signing. Must be at least 256 bits (32 characters)
     * for HS256.
     */
    private String secret;

    /**
     * JWT token expiration time.
     * Supports Spring Boot duration formats (e.g., 1h, 30m, 3600s).
     */
    private Duration expiration = Duration.ofHours(1); // Default to 1 hour

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Returns the expiration in milliseconds.
     */
    public long getExpiration() {
        return expiration.toMillis();
    }

    /**
     * Returns the raw Duration object.
     */
    public Duration getExpirationDuration() {
        return expiration;
    }

    /**
     * Internal setter for Spring Boot properties mapping.
     */
    public void setExpiration(Duration expiration) {
        if (expiration != null) {
            this.expiration = expiration;
        }
    }

    /**
     * Returns a human-readable string of the expiration.
     */
    public String getExpirationHumanReadable() {
        long hours = expiration.toHours();
        long minutes = expiration.toMinutesPart();

        if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            return expiration.toMillis() + " ms";
        }
    }
}
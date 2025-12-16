package com.devision.job_manager_company.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validate an access token and extract the company ID.
     *
     * @param token the JWT token (without "Bearer " prefix)
     * @return the company ID (subject) from the token
     * @throws IllegalArgumentException if token is invalid or expired
     */
    public UUID validateTokenAndGetCompanyId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verify this is an ACCESS token, not a REFRESH token
            String type = claims.get("type", String.class);
            if (!"ACCESS".equals(type)) {
                throw new IllegalArgumentException("Invalid token type: expected ACCESS token");
            }

            return UUID.fromString(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new IllegalArgumentException("Token has expired");
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    /**
     * Extract claims from the token without validation (for logging/debugging).
     * This should NOT be used for security decisions.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract email from token claims.
     */
    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Extract role from token claims.
     */
    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract token from Authorization header.
     *
     * @param authHeader the Authorization header value
     * @return the token without "Bearer " prefix, or null if invalid
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

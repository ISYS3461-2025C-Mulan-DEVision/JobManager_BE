package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.entity.Company;
import com.devision.job_manager_auth.service.internal.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateAccessToken(Company company) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        // Generate JWE encrypted token
        return Jwts.builder()
                .header()
                    .type("JWE")
                    .and()
                .subject(company.getId().toString())
                .claim("email", company.getEmail())
                .claim("role", company.getRole().name())
                .claim("country", company.getCountry().name())
                .claim("type", "ACCESS")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

    }

    @Override
    public String generateRefreshToken(Company company) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        // Generate a token with longer expiration
        return Jwts.builder()
                .subject(company.getId().toString())
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public Long validateAccessToken(String token) {
        try {
            // Check if token is revoked
            if (isTokenRevoked(token)) {
                throw new IllegalArgumentException("Token has been revoked");

            }

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (! "ACCESS".equals(type)) {
                throw new IllegalArgumentException("Invalid token type");
            }

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw new IllegalArgumentException("Token has expired");
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    @Override
    public Long validateRefreshToken(String token) {
        try {
            // Check if token is revoked
            if (isTokenRevoked(token)) {
                throw new IllegalArgumentException("Refresh token has been revoked");
            }

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verify it's a refresh token
            String type = claims.get("type", String.class);
            if (!"REFRESH".equals(type)) {
                throw new IllegalArgumentException("Invalid token type");
            }

            return Long.parseLong(claims.getSubject());

        } catch (ExpiredJwtException e) {
            log.error("Refresh token expired: {}", e.getMessage());
            throw new IllegalArgumentException("Refresh token has expired");
        } catch (JwtException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    // Token needs to be revoked as user logs out to avoid security issues
    @Override
    public void revokeToken(String token) {
        try {

            // Get the token expiration time
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Calculate the remaining time before expiration
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();

            // Only blacklist if token hasn't expired yet
            if (remainingTime > 0) {
                // Store token in Redis
                String key = "revoked:token:" + token;
                redisTemplate.opsForValue().set(key, "revoked", remainingTime, TimeUnit.MILLISECONDS);
                log.info("Token revoked and stored in Redis with TTL: {} ms", remainingTime);
            }

        } catch (JwtException e) {
            log.warn("Failed to revoke token: {}", e.getMessage());
            // Even if parsing fails, we can still blacklist the token
            String key = "revoked:token:" + token;
            redisTemplate.opsForValue().set(key, "revoked", accessTokenExpiration, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean isTokenRevoked(String token) {

        String key = "revoked:token:" + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}

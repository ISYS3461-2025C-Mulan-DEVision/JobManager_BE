package com.devision.job_manager_auth.service.internal;

import com.devision.job_manager_auth.entity.CompanyAccount;

import java.util.UUID;

public interface TokenService {
    String generateAccessToken(CompanyAccount account);

    String generateRefreshToken(CompanyAccount account);

    Long validateAccessToken(String token);

    UUID validateRefreshToken(String token);

    void revokeToken(String token);

    boolean isTokenRevoked(String token);

    String extractTokenFromHeader(String authHeader);
}

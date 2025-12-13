package com.devision.job_manager_auth.service.internal;

import com.devision.job_manager_auth.entity.CompanyAccount;

public interface TokenService {
    String generateAccessToken(CompanyAccount account);

    String generateRefreshToken(CompanyAccount account);

    Long validateAccessToken(String token);

    Long validateRefreshToken(String token);

    void revokeToken(String token);

    boolean isTokenRevoked(String token);

    String extractTokenFromHeader(String authHeader);
}

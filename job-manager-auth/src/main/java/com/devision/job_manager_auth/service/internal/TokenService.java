package com.devision.job_manager_auth.service.internal;

import com.devision.job_manager_auth.entity.Company;

public interface TokenService {
    String generateAccessToken(Company company);

    String generateRefreshToken(Company company);

    Long validateAccessToken(String token);

    Long validateRefreshToken(String token);

    void revokeToken(String token);

    boolean isTokenRevoked(String token);

    String extractTokenFromHeader(String authHeader);
}

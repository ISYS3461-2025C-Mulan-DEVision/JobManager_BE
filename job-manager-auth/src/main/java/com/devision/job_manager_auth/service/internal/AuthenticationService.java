package com.devision.job_manager_auth.service.internal;

import com.devision.job_manager_auth.dto.internal.*;
import com.devision.job_manager_auth.entity.Country;

public interface AuthenticationService {

    /**
     *
     * @param request
     * @return Success message
     * @throws IllegalArgumentException if email is found in database or validation fails
     */
    ApiResponse<String> registerCompany(RegisterRequest request);

    /**
     *
     * @param email
     * @param name
     * @param ssoProviderId
     * @return Success message
     */
    ApiResponse<String> registerCompanyViaSso(String email, String name, String ssoProviderId, Country country);

    /**
     *
     * @param request
     * @return Success message
     * @throws IllegalArgumentException if token is invalid or expired
     */
    ApiResponse<String> activateAccount(ActivationRequest request);

    /**
     *
     * @param request
     * @return Authentication response with JWT tokens
     * @throws IllegalArgumentException if credentials are invalid or account is locked
     */
    ApiResponse<AuthResponse> login(LoginRequest request);

    /**
     *
     * @param ssoProviderId
     * @return Authentication response with JWT tokens
     * @throws IllegalArgumentException if SSO failed
     */
    ApiResponse<AuthResponse> loginViaSso(String ssoProviderId);

    /**
     *
     * @param request Refresh token
     * @return new access token
     * @throws IllegalArgumentException if refresh token is invalid or expired
     */
    ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request);

    /**
     *
     * @param accessToken
     * @return Success message
     */
    ApiResponse<String> logout(String accessToken);

    /**
     *
     * @param email
     * @return Success message
     */
    ApiResponse<String> resendActivationEmail(String email);
}

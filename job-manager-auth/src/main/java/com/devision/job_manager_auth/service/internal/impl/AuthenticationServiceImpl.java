package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.dto.internal.*;
import com.devision.job_manager_auth.entity.Company;
import com.devision.job_manager_auth.entity.Role;
import com.devision.job_manager_auth.entity.SsoProvider;
import com.devision.job_manager_auth.repository.CompanyRepository;
import com.devision.job_manager_auth.service.internal.AuthenticationService;
import com.devision.job_manager_auth.service.internal.EmailService;
import com.devision.job_manager_auth.service.internal.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Value("${app.activation.token-expiration}")
    private long activationTokenExpiration;

    @Override
    @Transactional
    public ApiResponse<String> registerCompany(RegisterRequest request) {
        if (companyRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            return ApiResponse.error("Email already registered");
        }
        
        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plus(activationTokenExpiration, ChronoUnit.MILLIS);

        // Create company entity
        Company company = Company.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .name(request.getName())
                .country(request.getCountry())
                .phone(request.getPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .role(Role.COMPANY)
                .ssoProvider(SsoProvider.NONE) // Regular registration
                .isActivated(false) // user will then activate their account via email
                .activationToken(activationToken)
                .activationTokenExpiry(tokenExpiry)
                .failedLoginAttempts(0)
                .isLocked(false)
                .build();

        // Save company entity
        companyRepository.save(company);
        log.info("Company registered successfully: {}", request.getEmail());

        // Send activation email
        emailService.sendActivationEmail(company, activationToken);

        return ApiResponse.success(
                "Registration successful! Please check your email to activate your account.",
                null
        );

    }

    @Override
    @Transactional
    public ApiResponse<String> registerCompanyViaSso(String email, String name, String ssoProviderId) {

        // Check if the account already exists
        if (companyRepository.existsBySsoProviderAndSsoProviderId(SsoProvider.GOOGLE, ssoProviderId)) {
            log.warn("SSO registration failed: Account already exists - {}", email);
            return ApiResponse.error("SSO account already registered");
        }

        // Check if email is already used with regular registration
        if (companyRepository.existsByEmail(email)) {
            log.warn("SSO registration failed: Email already used with regular registration - {}", email);
            return ApiResponse.error("Email already registered with password login");
        }

        // Create company entity
        Company company = Company.builder()
                .email(email)
                .password(null) // SSO users don't have passwords
                .name(name)
                .country(null) // Will be set by user after first login
                .role(Role.COMPANY)
                .ssoProvider(SsoProvider.GOOGLE)
                .ssoProviderId(ssoProviderId)
                .isActivated(true) // SSO accounts are pre-activated (verified by Google)
                .activationToken(null)
                .activationTokenExpiry(null)
                .failedLoginAttempts(0)
                .isLocked(false)
                .build();

        companyRepository.save(company);
        log.info("SSO company registered successfully: {}", email);

        // Send welcome email
        emailService.sendWelcomeEmail(company);

        return ApiResponse.success("SSO registration successful!", null);

    }

    @Override
    @Transactional
    public ApiResponse<String> activateAccount(ActivationRequest request) {

        Company company = companyRepository.findByActivationToken(request.getToken())
                .orElseThrow(() -> {
                    log.warn("Activation failed: Invalid token");
                    return new IllegalArgumentException("Invalid activation token");
                });

        // Check if already activated
        if (company.getIsActivated()) {
            log.info("Account already activated: {}", company.getEmail());
            return ApiResponse.success("Account is already activated", null);
        }

        // Check if token expired
        if (company.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Activation failed: Token expired for {}", company.getEmail());
            return ApiResponse.error("Activation token has expired. Please request a new one.");
        }

        // Activate account
        companyRepository.activateAccount(company.getEmail());
        log.info("Account activated successfully: {}", company.getEmail());

        // Send welcome email
        emailService.sendWelcomeEmail(company);

        return ApiResponse.success("Account activated successfully! You can now login.", null);
    }

    @Override
    @Transactional
    public ApiResponse<String> resendActivationEmail(String email) {
        log.info("Resend activation email requested for: {}", email);

        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Resend activation failed: Email not found - {}", email);
                    return new IllegalArgumentException("Email not found");
                });

        // Check if already activated
        if (company.getIsActivated()) {
            log.info("Cannot resend: Account already activated - {}", email);
            return ApiResponse.error("Account is already activated");
        }

        // Generate new activation token
        String newToken = UUID.randomUUID().toString();
        LocalDateTime newExpiry = LocalDateTime.now().plus(activationTokenExpiration, ChronoUnit.MILLIS);

        company.setActivationToken(newToken);
        company.setActivationTokenExpiry(newExpiry);
        companyRepository.save(company);

        // Send new activation email
        emailService.sendActivationEmail(company, newToken);
        log.info("Activation email resent to: {}", email);

        return ApiResponse.success("Activation email sent! Please check your inbox.", null);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        // Find company by email
        Company company = companyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: Email not found - {}", request.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        // Check if account is locked
        if (company.getIsLocked()) {
            log.warn("Login failed: Account is locked - {}", request.getEmail());
            return ApiResponse.error("Account is locked due to multiple failed login attempts. Please try again later.");
        }

        // Check if account is activated
        if (!company.getIsActivated()) {
            log.warn("Login failed: Account not activated - {}", request.getEmail());
            return ApiResponse.error("Please activate your account first. Check your email for activation link.");
        }

        // Check if SSO user trying to login with password
        if (company.getSsoProvider() != SsoProvider.NONE) {
            log.warn("Login failed: SSO user trying to use password login - {}", request.getEmail());
            return ApiResponse.error("This account uses SSO login. Please login with " + company.getSsoProvider().name());
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), company.getPassword())) {
            log.warn("Login failed: Invalid password - {}", request.getEmail());
            handleFailedLogin(company);
            return ApiResponse.error("Invalid email or password");
        }

        // Reset failed login attempts on successful login
        if (company.getFailedLoginAttempts() > 0) {
            companyRepository.resetFailedLoginAttempts(company.getEmail());
        }


        String accessToken = tokenService.generateAccessToken(company);
        String refreshToken = tokenService.generateRefreshToken(company);

        log.info("Login successful for: {}", request.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 hours in seconds
                .companyId(company.getId())
                .email(company.getEmail())
                .companyName(company.getName())
                .country(company.getCountry())
                .role(company.getRole())
                .isSsoUser(false)
                .build();

        return ApiResponse.success("Login successful", authResponse);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> loginViaSso(String ssoProviderId) {
        log.info("SSO login attempt for provider ID: {}", ssoProviderId);

        Company company = companyRepository.findBySsoProviderAndSsoProviderId(SsoProvider.GOOGLE, ssoProviderId)
                .orElseThrow(() -> {
                    log.warn("SSO login failed: User not found with provider ID - {}", ssoProviderId);
                    return new IllegalArgumentException("SSO user not found. Please register first.");
                });

        // Check if account is locked
        if (company.getIsLocked()) {
            log.warn("SSO login failed: Account is locked - {}", company.getEmail());
            return ApiResponse.error("Account is locked. Please contact support.");
        }

        if (!company.getIsActivated()) {
            company.setIsActivated(true);
            companyRepository.save(company);
        }

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(company);
        String refreshToken = tokenService.generateRefreshToken(company);

        log.info("SSO login successful for: {}", company.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .companyId(company.getId())
                .email(company.getEmail())
                .companyName(company.getName())
                .country(company.getCountry())
                .role(company.getRole())
                .isSsoUser(true)
                .build();

        return ApiResponse.success("SSO login successful", authResponse);
    }

    private void handleFailedLogin(Company company) {
        LocalDateTime now = LocalDateTime.now();

        // Increment failed attempts
        companyRepository.incrementFailedLoginAttempts(company.getEmail(), now);

        int newFailedAttempts = company.getFailedLoginAttempts() + 1;

        // lock account when 5 failed attempts within 60 seconds)
        if (newFailedAttempts >= 5) {
            LocalDateTime lastFailedTime = company.getLastFailedLoginTime();

            if (lastFailedTime != null &&
                    lastFailedTime.isAfter(now.minusSeconds(60))) {
                // Lock account
                companyRepository.lockAccount(company.getEmail());
                emailService.sendAccountLockedEmail(company);
                log.warn("Account locked due to brute force: {}", company.getEmail());
            }
        }
    }

    @Override
    public ApiResponse<String> logout(String authHeader) {

        // Extract token from header
        String token = tokenService.extractTokenFromHeader(authHeader);

        if (token == null) {
            log.warn("Logout failed: No token provided");
            return ApiResponse.error("No token provided");
        }

        // Revoke token
        tokenService.revokeToken(token);

        log.info("Logout successful, token revoked");
        return ApiResponse.success("Logout successful", null);
    }

    @Override
    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        try {
            // Validate refresh token
            Long companyId = tokenService.validateRefreshToken(request.getRefreshToken());

            // Get company
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Company not found"));

            // Check if account is still active
            if (!company.getIsActivated() || company.getIsLocked()) {
                log.warn("Token refresh failed: Account inactive or locked - {}", company.getEmail());
                return ApiResponse.error("Account is not active");
            }

            // Generate new access token
            String newAccessToken = tokenService.generateAccessToken(company);

            log.info("Token refreshed successfully for: {}", company.getEmail());

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(request.getRefreshToken()) // Keep same refresh token
                    .tokenType("Bearer")
                    .expiresIn(86400L)
                    .companyId(company.getId())
                    .email(company.getEmail())
                    .companyName(company.getName())
                    .country(company.getCountry())
                    .role(company.getRole())
                    .isSsoUser(company.getSsoProvider() != SsoProvider.NONE)
                    .build();

            return ApiResponse.success("Token refreshed successfully", authResponse);

        } catch (IllegalArgumentException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

}

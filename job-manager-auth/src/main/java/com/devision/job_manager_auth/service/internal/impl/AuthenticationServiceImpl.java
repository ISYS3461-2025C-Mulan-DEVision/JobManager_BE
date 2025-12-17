package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.dto.internal.*;
import com.devision.job_manager_auth.entity.AuthProvider;
import com.devision.job_manager_auth.entity.CompanyAccount;
import com.devision.job_manager_auth.entity.Country;
import com.devision.job_manager_auth.entity.Role;
import com.devision.job_manager_auth.event.CompanyActivatedEvent;
import com.devision.job_manager_auth.event.CompanyAccountLockedEvent;
import com.devision.job_manager_auth.event.CompanyRegisteredEvent;
import com.devision.job_manager_auth.repository.CompanyAccountRepository;
import com.devision.job_manager_auth.service.internal.AuthenticationService;
import com.devision.job_manager_auth.service.internal.EmailService;
import com.devision.job_manager_auth.service.internal.EventPublisherService;
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

    private final CompanyAccountRepository companyAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EventPublisherService eventPublisherService;
    private final EmailService emailService;

    @Value("${app.activation.token-expiration}")
    private long activationTokenExpiration;

    @Override
    @Transactional
    public ApiResponse<String> registerCompany(RegisterRequest request) {
        if (companyAccountRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            return ApiResponse.error("Email already registered");
        }
        
        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plus(activationTokenExpiration, ChronoUnit.MILLIS);

        // Create company account entity (auth only)
        CompanyAccount account = CompanyAccount.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .authProvider(AuthProvider.LOCAL)
                .role(Role.COMPANY)
                .country(request.getCountry())
                .isActivated(false)
                .activationToken(activationToken)
                .activationTokenExpiry(tokenExpiry)
                .failedLoginAttempts(0)
                .isLocked(false)
                .build();

        // Save account
        account = companyAccountRepository.save(account);
        log.info("Company account registered successfully: {}", request.getEmail());

        // Publish event for Company Service to create profile and for Email Service to send activation
        CompanyRegisteredEvent event = CompanyRegisteredEvent.builder()
                .companyId(account.getId())
                .email(request.getEmail())
                .countryCode(request.getCountry() != null ? request.getCountry().getCode() : null)
                .activationToken(activationToken)
                .registeredAt(LocalDateTime.now())
                .build();


        eventPublisherService.publishCompanyRegistered(event);

        emailService.sendActivationEmail(account, activationToken);

        return ApiResponse.success(
                "Registration successful! Please check your email to activate your account.",
                null
        );
    }

    @Override
    @Transactional
    public ApiResponse<String> registerCompanyViaSso(String email, String name, String ssoProviderId, Country country) {

        // Check if the account already exists
        if (companyAccountRepository.existsByAuthProviderAndSsoProviderId(AuthProvider.GOOGLE, ssoProviderId)) {
            log.warn("SSO registration failed: Account already exists - {}", email);
            return ApiResponse.error("SSO account already registered");
        }

        // Check if email is already used with regular registration
        if (companyAccountRepository.existsByEmail(email)) {
            log.warn("SSO registration failed: Email already used with regular registration - {}", email);
            return ApiResponse.error("Email already registered with password login");
        }

        // Create company account entity
        CompanyAccount account = CompanyAccount.builder()
                .email(email)
                .passwordHash(null) // SSO users don't have passwords
                .country(country)
                .authProvider(AuthProvider.GOOGLE)
                .ssoProviderId(ssoProviderId)
                .role(Role.COMPANY)
                .isActivated(true) // SSO accounts are pre-activated (verified by Google)
                .activationToken(null)
                .activationTokenExpiry(null)
                .failedLoginAttempts(0)
                .isLocked(false)
                .build();

        account = companyAccountRepository.save(account);
        log.info("SSO company account registered successfully: {}", email);

        // Publish event for Company Service to create profile
        CompanyRegisteredEvent event = CompanyRegisteredEvent.builder()
                .companyId(account.getId())
                .email(email)
                .registeredAt(LocalDateTime.now())
                .build();

        eventPublisherService.publishCompanyRegistered(event);

        return ApiResponse.success("SSO registration successful!", null);
    }

    @Override
    @Transactional
    public ApiResponse<String> activateAccount(ActivationRequest request) {

        CompanyAccount account = companyAccountRepository.findByActivationToken(request.getToken())
                .orElseThrow(() -> {
                    log.warn("Activation failed: Invalid token");
                    return new IllegalArgumentException("Invalid activation token");
                });

        // Check if already activated
        if (account.getIsActivated()) {
            log.info("Account already activated: {}", account.getEmail());
            return ApiResponse.success("Account is already activated", null);
        }

        // Check if token expired
        if (account.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Activation failed: Token expired for {}", account.getEmail());
            return ApiResponse.error("Activation token has expired. Please request a new one.");
        }

        // Activate account
        companyAccountRepository.activateAccount(account.getEmail());
        log.info("Account activated successfully: {}", account.getEmail());

        // Publish activation event
        CompanyActivatedEvent event = CompanyActivatedEvent.builder()
                .companyId(account.getId())
                .email(account.getEmail())
                .activatedAt(LocalDateTime.now())
                .build();
        eventPublisherService.publishCompanyActivated(event);

        emailService.sendWelcomeEmail(account);

        return ApiResponse.success("Account activated successfully! You can now login.", null);
    }

    @Override
    @Transactional
    public ApiResponse<String> resendActivationEmail(String email) {
        log.info("Resend activation email requested for: {}", email);

        CompanyAccount account = companyAccountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Resend activation failed: Email not found - {}", email);
                    return new IllegalArgumentException("Email not found");
                });

        // Check if already activated
        if (account.getIsActivated()) {
            log.info("Cannot resend: Account already activated - {}", email);
            return ApiResponse.error("Account is already activated");
        }

        // Generate new activation token
        String newToken = UUID.randomUUID().toString();
        LocalDateTime newExpiry = LocalDateTime.now().plus(activationTokenExpiration, ChronoUnit.MILLIS);

        account.setActivationToken(newToken);
        account.setActivationTokenExpiry(newExpiry);
        companyAccountRepository.save(account);

        // Publish event for email service to resend activation email
        CompanyRegisteredEvent event = CompanyRegisteredEvent.builder()
                .companyId(account.getId())
                .email(account.getEmail())
                .activationToken(newToken)
                .registeredAt(LocalDateTime.now())
                .build();
        eventPublisherService.publishCompanyRegistered(event);

        emailService.sendActivationEmail(account, newToken);

        log.info("Activation email resent to: {}", email);

        return ApiResponse.success("Activation email sent! Please check your inbox.", null);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        // Find account by email
        CompanyAccount account = companyAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: Email not found - {}", request.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        // Check if account is locked
        if (account.getIsLocked()) {
            log.warn("Login failed: Account is locked - {}", request.getEmail());
            return ApiResponse.error("Account is locked due to multiple failed login attempts. Please try again later.");
        }

        // Check if account is activated
        if (!account.getIsActivated()) {
            log.warn("Login failed: Account not activated - {}", request.getEmail());
            return ApiResponse.error("Please activate your account first. Check your email for activation link.");
        }

        // Check if SSO user trying to login with password
        if (account.getAuthProvider() != AuthProvider.LOCAL) {
            log.warn("Login failed: SSO user trying to use password login - {}", request.getEmail());
            return ApiResponse.error("This account uses SSO login. Please login with " + account.getAuthProvider().name());
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            log.warn("Login failed: Invalid password - {}", request.getEmail());
            handleFailedLogin(account);
            return ApiResponse.error("Invalid email or password");
        }

        // Reset failed login attempts on successful login
        if (account.getFailedLoginAttempts() > 0) {
            companyAccountRepository.resetFailedLoginAttempts(account.getEmail());
        }

        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        log.info("Login successful for: {}", request.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 hours in seconds
                .companyId(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .authProvider(account.getAuthProvider())
                .build();

        return ApiResponse.success("Login successful", authResponse);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> loginViaSso(String ssoProviderId) {
        log.info("SSO login attempt for provider ID: {}", ssoProviderId);

        CompanyAccount account = companyAccountRepository.findByAuthProviderAndSsoProviderId(AuthProvider.GOOGLE, ssoProviderId)
                .orElseThrow(() -> {
                    log.warn("SSO login failed: User not found with provider ID - {}", ssoProviderId);
                    return new IllegalArgumentException("SSO user not found. Please register first.");
                });

        // Check if account is locked
        if (account.getIsLocked()) {
            log.warn("SSO login failed: Account is locked - {}", account.getEmail());
            return ApiResponse.error("Account is locked. Please contact support.");
        }

        if (!account.getIsActivated()) {
            account.setIsActivated(true);
            companyAccountRepository.save(account);
        }

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(account);
        String refreshToken = tokenService.generateRefreshToken(account);

        log.info("SSO login successful for: {}", account.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .companyId(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .authProvider(account.getAuthProvider())
                .build();

        return ApiResponse.success("SSO login successful", authResponse);
    }

    private void handleFailedLogin(CompanyAccount account) {
        LocalDateTime now = LocalDateTime.now();

        // Increment failed attempts
        companyAccountRepository.incrementFailedLoginAttempts(account.getEmail(), now);

        int newFailedAttempts = account.getFailedLoginAttempts() + 1;

        // lock account when 5 failed attempts within 60 seconds
        if (newFailedAttempts >= 5) {
            LocalDateTime lastFailedTime = account.getLastFailedLoginTime();

            if (lastFailedTime != null &&
                    lastFailedTime.isAfter(now.minusSeconds(60))) {
                // Lock account
                companyAccountRepository.lockAccount(account.getEmail());
                
                // Publish account locked event
                CompanyAccountLockedEvent event = CompanyAccountLockedEvent.builder()
                        .companyId(account.getId())
                        .email(account.getEmail())
                        .reason("Multiple failed login attempts")
                        .lockedAt(now)
                        .build();
                eventPublisherService.publishCompanyAccountLocked(event);

                emailService.sendAccountLockedEmail(account);
                
                log.warn("Account locked due to brute force: {}", account.getEmail());
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
            UUID accountId = tokenService.validateRefreshToken(request.getRefreshToken());

            // Get account
            CompanyAccount account = companyAccountRepository.findById(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            // Check if account is still active
            if (!account.getIsActivated() || account.getIsLocked()) {
                log.warn("Token refresh failed: Account inactive or locked - {}", account.getEmail());
                return ApiResponse.error("Account is not active");
            }

            // Generate new access token
            String newAccessToken = tokenService.generateAccessToken(account);

            log.info("Token refreshed successfully for: {}", account.getEmail());

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(request.getRefreshToken()) // Keep same refresh token
                    .tokenType("Bearer")
                    .expiresIn(86400L)
                    .companyId(account.getId())
                    .email(account.getEmail())
                    .role(account.getRole())
                    .authProvider(account.getAuthProvider())
                    .build();

            return ApiResponse.success("Token refreshed successfully", authResponse);

        } catch (IllegalArgumentException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}

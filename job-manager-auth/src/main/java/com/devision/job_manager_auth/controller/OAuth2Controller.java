package com.devision.job_manager_auth.controller;

import com.devision.job_manager_auth.dto.internal.ApiResponse;
import com.devision.job_manager_auth.dto.internal.AuthResponse;
import com.devision.job_manager_auth.service.internal.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {
    private final AuthenticationService authenticationService;

    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> handleOAuth2Callback(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam(required = false) String country
    ) {
        log.info("OAuth2 callback received");

        // Extract user info from OAuth2User
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String ssoProviderId = (String) attributes.get("sub"); // Google's user Id

        log.info("OAuth2 user: email={}, name={}, ssoProviderId={}", email, name, ssoProviderId);

        // If user exists ---> login. Otherwise ---> register
        ApiResponse<AuthResponse> response;

        try {
            response = authenticationService.loginViaSso(ssoProviderId);
            log.info("Existing SSO user logged in: {}", email);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.info("New SSO user registered: {}", email);

            // For SSO registration, country should be provided by frontend
            if (country == null || country.isBlank()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Country is required for new SSO registration")
                );
            }

            ApiResponse<String> registrationResponse = authenticationService.registerCompanyViaSso(
                    email, name, ssoProviderId
            );

            // After registration, log the user in
            response = authenticationService.loginViaSso(ssoProviderId);
            log.info("New SSO user registered and logged in: {}", email);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    // Not necessary
    @GetMapping("/login/google")
    public ResponseEntity<ApiResponse<String>> initiateGoogleLogin() {

        // Spring Security will handle the redirect automatically
        return ResponseEntity.ok(ApiResponse.success(
                "Redirect to Google OAuth2 authorization",
                "/oauth2/authorization/google"
        ));
    }
}

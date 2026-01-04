package com.devision.job_manager_auth.controller;


import com.devision.job_manager_auth.dto.external.TokenValidationResponse;
import com.devision.job_manager_auth.service.internal.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class TokenValidationController {

    private final TokenService tokenService;

    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenValidationResponse.invalid());
            }

            String token = authHeader.substring(7);

            // Check if token is revoked
            if (tokenService.isTokenRevoked(token)) {
                log.warn("Token is revoked");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenValidationResponse.invalid());
            }

            // Validate token and extract claims
            Claims claims = tokenService.extractAllClaims(token);

            if (claims == null) {
                log.warn("Token validation failed - invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenValidationResponse.invalid());
            }

            // Check token type
            String tokenType = claims.get("type", String.class);
            if (!"ACCESS".equals(tokenType)) {
                log.warn("Invalid token type: {}", tokenType);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenValidationResponse.invalid());
            }

            // Extract info
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            String countryCode = claims.get("country", String.class);

            // Build response
            TokenValidationResponse response = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .email(email)
                    .role(role)
                    .countryCode(countryCode)
                    .build();

            log.debug("Token validated successfully for user: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TokenValidationResponse.invalid());
        }
    }

}

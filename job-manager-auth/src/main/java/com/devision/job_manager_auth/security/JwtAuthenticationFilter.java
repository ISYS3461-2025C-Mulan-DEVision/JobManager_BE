package com.devision.job_manager_auth.security;


import com.devision.job_manager_auth.entity.CompanyAccount;
import com.devision.job_manager_auth.repository.CompanyAccountRepository;
import com.devision.job_manager_auth.service.internal.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final CompanyAccountRepository companyAccountRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extract JWT token from auth header
            String authHeader = request.getHeader("Authorization");
            String token = tokenService.extractTokenFromHeader(authHeader);

            // Validate that token
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Long companyId = tokenService.validateAccessToken(token);

                    // Check if the token is revoked in Redis
                    if (tokenService.isTokenRevoked(token)) {
                        log.warn("Token has been revoked, user ID: {}", companyId);
                    } else {
                        // Load company from database
                        CompanyAccount company = companyAccountRepository.findByI
                    }
                }
            }
        }
    }
}

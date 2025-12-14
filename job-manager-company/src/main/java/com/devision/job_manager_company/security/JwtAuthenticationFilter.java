package com.devision.job_manager_company.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Validate token and get company ID
                UUID companyId = jwtUtil.validateTokenAndGetCompanyId(token);

                // Extract additional claims for authorization
                String role = jwtUtil.getRoleFromToken(token);
                String email = jwtUtil.getEmailFromToken(token);

                // Create authentication token with company ID as principal
                // The principal is the CompanyPrincipal which holds company ID and email
                CompanyPrincipal principal = new CompanyPrincipal(companyId, email, role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null, // No credentials needed - token is already validated
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated company ID: {} with role: {}", companyId, role);
            }
        } catch (IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            // Don't set authentication - request will be rejected by security config if needed
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for health check and actuator endpoints
        return path.equals("/api/companies/health") || 
               path.startsWith("/actuator");
    }
}

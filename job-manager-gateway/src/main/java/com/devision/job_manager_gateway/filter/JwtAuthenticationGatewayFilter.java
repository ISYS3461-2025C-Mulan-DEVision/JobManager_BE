package com.devision.job_manager_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    // For debugging purposes
    @Override
    public String name() {
        return "JwtAuthenticationGatewayFilter";
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    public JwtAuthenticationGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip authentication for public endpoints
            if (isPublicEndpoint(request.getPath().value())) {
                return chain.filter(exchange);
            }

            // Extract token from Auth header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // Validate token with Auth Service
            return validateTokenWithAuthService(token)
                    .flatMap(validationResponse -> {
                        if (validationResponse.isValid()) {
                            // Add user info to request headers for downstream services
                            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Id", validationResponse.getUserId())
                                    .header("X-User-Email", validationResponse.getEmail())
                                    .header("X-User-Role", validationResponse.getRole())
                                    .header("X-Country-Code", validationResponse.getCountryCode())
                                    .build();

                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        } else {
                            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .onErrorResume(error -> {
                        System.err.println("Error validating token: " + error.getMessage());
                        return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
                    });
        };
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/activate") ||
                path.startsWith("/api/auth/resend-activation") ||
                path.startsWith("/api/auth/forgot-password") ||
                path.startsWith("/api/auth/reset-password") ||
                path.startsWith("/api/auth/countries") ||
                path.startsWith("/api/auth/oauth2") ||
                path.startsWith("/api/auth/health") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/complete") ||
                path.startsWith("/api/auth/diagnostics") ||
                path.startsWith("/actuator") ||
                path.startsWith("/health");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    private Mono<TokenValidationResponse> validateTokenWithAuthService(String token) {
        return webClientBuilder.build()
                .post()
                .uri("http://job-manager-auth/api/auth/validate-token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(TokenValidationResponse.class);
    }

    public static class TokenValidationResponse {
        private boolean valid;
        private String userId;
        private String email;
        private String role;
        private String countryCode;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}

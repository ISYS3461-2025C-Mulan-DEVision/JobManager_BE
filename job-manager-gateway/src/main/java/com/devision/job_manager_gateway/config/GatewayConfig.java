package com.devision.job_manager_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Route
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8081"))

                // Company Service Route
                .route("company-service", r -> r
                        .path("/api/companies/**")
                        .uri("http://localhost:8082"))

                // Job Post Service Route
                .route("jobpost-service", r -> r
                        .path("/api/job-posts/**")
                        .uri("http://localhost:8083"))

                // Search Application Service Route
                .route("search-applicant-service", r -> r
                        .path("api/search-applicant/**")
                        .uri("http://localhost:8084"))

                // Notification Service Route
                .route("notification-service", r -> r
                        .path("api/notification/**")
                        .uri("http://localhost:8087"))

                // Payment Service Route
                .route("payment-service", r -> r
                        .path("api/payment/**")
                        .uri("http://localhost:8086"))

                // Subscription Service Route
                .route("subscription-service", r -> r
                        .path("api/subscription/**")
                        .uri("http://localhost:8085"))

                // Build more routes as needed
                .build();
    }
}

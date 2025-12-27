package com.devision.job_manager_gateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth-service")
                .route(path("/api/auth/**"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> companyServiceRoute() {
        return route("company-service")
                .route(path("/api/companies/**"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> jobpostServiceRoute() {
        return route("jobpost-service")
                .route(path("/api/job-posts/**"), HandlerFunctions.http("http://localhost:8083"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> applicantSearchServiceRoute() {
        return route("applicant-search-service")
                .route(path("/api/search-profiles/**"), HandlerFunctions.http("http://localhost:8084"))
                .route(path("/api/internal/search-profiles/**"), HandlerFunctions.http("http://localhost:8084"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> subscriptionServiceRoute() {
        return route("subscription-service")
                .route(path("/api/subscriptions/**"), HandlerFunctions.http("http://localhost:8085"))
                .route(path("/api/external/subscriptions/**"), HandlerFunctions.http("http://localhost:8085"))
                .route(path("/api/internal/subscriptions/**"), HandlerFunctions.http("http://localhost:8085"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute() {
        return route("payment-service")
                .route(path("/api/payments/**"), HandlerFunctions.http("http://localhost:8086"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return route("notification-service")
                .route(path("/api/notifications/**"), HandlerFunctions.http("http://localhost:8087"))
                .build();
    }
}

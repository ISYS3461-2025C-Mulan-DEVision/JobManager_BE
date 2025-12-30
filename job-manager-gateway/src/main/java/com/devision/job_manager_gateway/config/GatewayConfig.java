//package com.devision.job_manager_gateway.config;
//
//import com.devision.job_manager_gateway.filter.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.function.RequestPredicates;
//import org.springframework.web.servlet.function.RouterFunction;
//import org.springframework.web.servlet.function.ServerResponse;
//
//// Correct Gateway MVC imports - note the 'webmvc' package
//import static org.springframework.cloud.gateway.server.webmvc.handler.GatewayRouterFunctions.route;
//import static org.springframework.cloud.gateway.server.webmvc.handler.HandlerFunctions.http;
//import static org.springframework.cloud.gateway.server.webmvc.starter.filter.BeforeFilterFunctions.*;
//
//@Configuration
//@RequiredArgsConstructor
//public class GatewayConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Bean
//    public RouterFunction<ServerResponse> gatewayRoutes() {
//        return route("auth-service")
//                .route(RequestPredicates.path("/api/auth/**"),
//                        HandlerFunctions.http("http://job-manager-auth"))
//                .filter(BeforeFilterFunctions.removeRequestHeader("Cookie"))
//                .build()
//
//                .and(route("company-service")
//                        .route(RequestPredicates.path("/api/companies/**"),
//                                HandlerFunctions.http("http://job-manager-company"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("jobpost-service")
//                        .route(RequestPredicates.path("/api/job-posts/**"),
//                                HandlerFunctions.http("http://job-manager-jobpost"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("search-applicant-service")
//                        .route(RequestPredicates.path("/api/search/**"),
//                                HandlerFunctions.http("http://job-manager-applicant-search"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("subscription-service")
//                        .route(RequestPredicates.path("/api/subscription/**"),
//                                HandlerFunctions.http("http://job-manager-subscription"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("payment-service")
//                        .route(RequestPredicates.path("/api/payment/**"),
//                                HandlerFunctions.http("http://job-manager-payment"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("notification-service")
//                        .route(RequestPredicates.path("/api/notification/**"),
//                                HandlerFunctions.http("http://job-manager-notification"))
//                        .filter(jwtAuthenticationFilter.validateToken())
//                        .build())
//
//                .and(route("external-api")
//                        .route(RequestPredicates.path("/api/external/**"),
//                                HandlerFunctions.http("http://job-manager-auth"))
//                        .filter(BeforeFilterFunctions.removeRequestHeader("Cookie"))
//                        .build())
//
//                .and(route("actuator")
//                        .route(RequestPredicates.path("/actuator/**"),
//                                HandlerFunctions.http("http://localhost:8080"))
//                        .build());
//    }
//}

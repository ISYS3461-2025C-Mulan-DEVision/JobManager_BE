package com.devision.job_manager_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow your frontend origin
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:5173",          // Development frontend
                "http://localhost",               // Docker frontend (port 80)
                "http://localhost:80",            // Docker frontend (explicit port)
                "http://52.76.250.138:5173",      // EC2 Frontend deployment
                "http://52.76.250.138",           // EC2 Frontend (port 80)
                "http://52.76.250.138:80",         // EC2 Frontend (explicit port 80)
                "https://52.76.250.138:443"
        ));

        // Allow all HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allow all headers
        corsConfig.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // Expose Authorization header to frontend
        corsConfig.setExposedHeaders(List.of("Authorization"));

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

package com.devision.job_manager_auth.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    // CORS is handled by the API Gateway - do not configure it here to avoid duplicate headers
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(Arrays.asList(
    //             "http://localhost:5173"
    //     ));
    //     configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    //     configuration.setAllowedHeaders(Arrays.asList("*"));
    //     configuration.setAllowCredentials(true);
    //     configuration.setExposedHeaders(Arrays.asList("Authorization"));
    //     configuration.setMaxAge(3600L);
    //
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())  // CORS handled by API Gateway
                .csrf(csrf -> csrf.disable()) // use JWT tokens instead
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/activate",
                                "/api/auth/resend-activation",
                                "/api/auth/countries",
                                "/api/auth/oauth2/**",
                                "/api/auth/health",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/external/**",
                                "/api/auth/logout",
                                "/api/auth/refresh",
                                "/api/auth/complete",

                                // External endpoints
                                "/api/external/**"
                        ).permitAll()

                        // Requiring authentication endpoints:
                        .anyRequest().authenticated()

                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );
        return http.build();
    }
}

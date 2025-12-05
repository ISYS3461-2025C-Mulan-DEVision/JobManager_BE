package com.devision.job_manager_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Use BCrypt to encrypt user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // use JWT tokens instead
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/activate",
                                "/api/auth/resend-activation",
                                "/api/auth/oauth2/**",
                                "/api/auth/health",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/external/**",
                                "/api/auth/logout",
                                "/api/auth/refresh"
                        ).permitAll()

                        // Requiring authentication endpoints:
                        .anyRequest().authenticated()

                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/api/auth/oauth2/callback", true)
//                );
        return http.build();
    }
}

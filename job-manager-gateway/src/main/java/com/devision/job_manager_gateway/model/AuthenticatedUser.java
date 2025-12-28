package com.devision.job_manager_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final String email;
    private final UUID userId;
    private final String role;
    private final String countryCode;

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}

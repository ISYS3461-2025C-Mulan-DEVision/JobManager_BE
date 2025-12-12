package com.devision.job_manager_auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a new company account is registered.
 * Consumed by Company Service to create profile and Email Service to send activation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRegisteredEvent {
    private UUID companyId;
    private String email;
    private String countryCode;
    private String activationToken;
    private LocalDateTime registeredAt;
}

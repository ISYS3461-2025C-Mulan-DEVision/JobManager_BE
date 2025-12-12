package com.devision.job_manager_company.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event received when a new company account is registered.
 * Triggers creation of company profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRegisteredEvent {
    private Long companyId;
    private String email;
    private String name;
    private String phone;
    private String streetAddress;
    private String city;
    private String countryCode;
    private String activationToken;
    private LocalDateTime registeredAt;
}

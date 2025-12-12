package com.devision.job_manager_auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a company account is activated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyActivatedEvent {
    private Long companyId;
    private String email;
    private LocalDateTime activatedAt;
}

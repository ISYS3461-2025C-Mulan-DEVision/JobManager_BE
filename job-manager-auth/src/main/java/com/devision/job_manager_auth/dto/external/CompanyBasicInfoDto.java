package com.devision.job_manager_auth.dto.external;

import com.devision.job_manager_auth.entity.AuthProvider;
import com.devision.job_manager_auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for external services to get basic auth info.
 * Profile info (name, country, city) is now in the Company service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyBasicInfoDto {
    private Long id;
    private String email;
    private Role role;
    private AuthProvider authProvider;
    private boolean isActivated;
    private LocalDateTime createdAt;
}

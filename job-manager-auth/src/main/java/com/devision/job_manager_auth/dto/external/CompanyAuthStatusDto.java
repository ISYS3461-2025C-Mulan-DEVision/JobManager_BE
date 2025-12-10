package com.devision.job_manager_auth.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAuthStatusDto {

    // This status is needed by other microservices

    private Long companyId;
    private String email;
    private boolean isActivated;
    private boolean isLocked;
    private boolean isSsoUser;
}

package com.devision.job_manager_auth.dto.external;


import com.devision.job_manager_auth.entity.Country;
import com.devision.job_manager_auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyBasicInfoDto {
    private Long id;
    private String email;
    private String companyName;
    private Country country;
    private String city;
    private Role role;
    private boolean isActivated;
    private LocalDateTime createdAt;
}

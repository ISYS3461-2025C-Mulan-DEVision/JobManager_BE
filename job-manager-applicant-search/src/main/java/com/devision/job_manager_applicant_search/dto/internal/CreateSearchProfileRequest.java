package com.devision.job_manager_applicant_search.dto.internal;

import com.devision.job_manager_applicant_search.model.EducationDegree;
import com.devision.job_manager_applicant_search.model.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSearchProfileRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Profile name is required")
    @Size(max = 255, message = "Profile name must be less than 255 characters")
    private String profileName;

    @Size(min = 2, max = 2, message = "Country code must be 2 characters (ISO 3166-1 alpha-2)")
    private String countryCode;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private EducationDegree highestDegree;

    private Set<EmploymentType> employmentTypes;

    private Set<UUID> skillIds;

    @Builder.Default
    private Boolean isActive = true;
}

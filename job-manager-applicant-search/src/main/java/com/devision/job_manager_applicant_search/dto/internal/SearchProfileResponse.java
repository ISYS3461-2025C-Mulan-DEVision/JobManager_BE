package com.devision.job_manager_applicant_search.dto.internal;

import com.devision.job_manager_applicant_search.model.ApplicantSearchProfile;
import com.devision.job_manager_applicant_search.model.EducationDegree;
import com.devision.job_manager_applicant_search.model.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProfileResponse {

    private UUID id;
    private UUID companyId;
    private String profileName;
    private String countryCode;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private EducationDegree highestDegree;
    private Set<EmploymentType> employmentTypes;
    private Set<UUID> skillIds;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static SearchProfileResponse fromEntity(ApplicantSearchProfile profile) {
        return SearchProfileResponse.builder()
                .id(profile.getId())
                .companyId(profile.getCompanyId())
                .profileName(profile.getProfileName())
                .countryCode(profile.getCountryCode())
                .minSalary(profile.getMinSalary())
                .maxSalary(profile.getMaxSalary())
                .highestDegree(profile.getHighestDegree())
                .employmentTypes(profile.getEmploymentTypeValues())
                .skillIds(profile.getSkillIds())
                .isActive(profile.getIsActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

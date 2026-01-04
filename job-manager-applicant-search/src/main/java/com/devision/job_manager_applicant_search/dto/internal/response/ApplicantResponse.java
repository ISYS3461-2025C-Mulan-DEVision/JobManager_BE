package com.devision.job_manager_applicant_search.dto.internal.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for applicant data.
 * Maps JA service's UserResponse to our domain model.
 * 
 * TODO: Applicant Data Dependency
 * The exact structure of applicant data is owned by the Job Applicant team.
 * Field names may change when the JA service is updated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantResponse {

    private UUID id;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String fullName;
    
    private String phone;
    
    // User's objective summary (bio)
    private String objectiveSummary;
    
    private String avatarUrl;
    
    private boolean premium;
    
    private boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime profileUpdatedAt;
    
    // Nested country object from JA.
    private CountryDto country;
    
    // Skills list - populated from JA user skills endpoint if needed.
    private List<SkillDto> skills;

    // Nested country DTO matching JA's CountryResponse.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountryDto {
        private String id;
        private String name;
        private String abbreviation;
    }

    // Skill DTO matching JA's SkillResponse.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDto {
        private String id;
        private String name;
        private int usageCount;
    }
}

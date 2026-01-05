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
 * Aligned with JA service's UserResponse.
 * 
 * Fields match JA's UserResponse structure as of 2026-01-04.
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
    
    /**
     * Street address.
     */
    private String address;
    
    /**
     * City name.
     */
    private String city;
    
    /**
     * User's objective summary (bio).
     */
    private String objectiveSummary;
    
    private String avatarUrl;
    
    private boolean premium;
    
    private boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime profileUpdatedAt;
    
    /**
     * Nested country object from JA.
     */
    private CountryDto country;
    
    /**
     * Skills list from JA.
     */
    private List<SkillDto> skills;
    
    /**
     * Education history from JA.
     */
    private List<EducationDto> education;
    
    /**
     * Work experience history from JA.
     */
    private List<WorkExperienceDto> workExperience;
    
    /**
     * Portfolio items from JA.
     */
    private List<PortfolioItemDto> portfolioItems;

    // ==================== Nested DTOs ====================

    /**
     * Country DTO matching JA's CountryResponse.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountryDto {
        private String id;
        private String name;
        private String abbreviation;
    }

    /**
     * Skill DTO matching JA's SkillResponse.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDto {
        private String id;
        private String name;
        private int usageCount;
    }

    /**
     * Education DTO matching JA's UserEducationResponse.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EducationDto {
        private UUID id;
        private String educationLevel; // HIGH_SCHOOL, ASSOCIATE, BACHELOR, MASTER, DOCTORATE
        private String fieldOfStudy;
        private String institutionName;
        private String startAt; // ISO date string
        private String endAt;
        private String description;
    }

    /**
     * Work experience DTO matching JA's UserWorkExperienceResponse.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkExperienceDto {
        private UUID id;
        private String title;
        private String companyName;
        private String employmentType; // FULL_TIME, PART_TIME, etc.
        private String startAt;
        private String endAt;
        private boolean currentJob;
        private String description;
        private CountryDto country;
    }

    /**
     * Portfolio item DTO matching JA's UserPortfolioItemResponse.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PortfolioItemDto {
        private UUID id;
        private String title;
        private String description;
        private String url;
        private String imageUrl;
    }
}

package com.devision.job_manager_applicant_search.dto.internal.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for applicant search filters.
 * Maps frontend filter state to backend search parameters.
 * 
 * TODO: Salary Filtering
 * JA service currently does not have salary fields in UserResponse.
 * When JA adds salary support, uncomment minSalary and maxSalary fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSearchRequest {

    // Full-text keyword search (name, email, bio, skills)
    private String keyword;

    // Country code filter (e.g., "US", "VN")
    private String countryCode;

    // Skill names to filter by (OR semantics)
    private List<String> skills;

    /**
     * Employment types to filter by.
     * Note: JA does not have employment type in user search.
     * This will be used for client-side filtering if needed.
     */
    private List<String> employmentTypes;

    /**
     * Highest education degree filter.
     * Note: JA does not have education filter in user search.
     */
    private String highestDegree;

    // Sort option (e.g., "newest", "salaryAsc", "salaryDesc")
    private String sortBy;

    // Page number (0-indexed)
    private Integer page;

    // Page size
    private Integer pageSize;

    // TODO: Salary filtering - uncomment when JA adds salary support
    // private BigDecimal minSalary;
    // private BigDecimal maxSalary;
}

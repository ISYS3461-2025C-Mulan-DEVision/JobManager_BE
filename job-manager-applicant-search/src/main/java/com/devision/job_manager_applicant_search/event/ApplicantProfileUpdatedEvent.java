package com.devision.job_manager_applicant_search.event;

import com.devision.job_manager_applicant_search.model.EducationDegree;
import com.devision.job_manager_applicant_search.model.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Kafka event received when an applicant updates their profile.
 * 
 * TODO: Applicant Data Dependency:
 * The exact structure of applicant data (profile fields, skills representation,
 * education mapping, etc.) is owned by the Job Applicant team.
 * Field names, enum values, and nesting may change once the Job Applicant subsystem is finalized.
 * This DTO must be updated accordingly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantProfileUpdatedEvent {

    private UUID applicantId;
    
    private String countryCode;
    
    private EducationDegree highestDegree;
    
    private BigDecimal desiredSalary;
    
    private Set<EmploymentType> employmentTypes;
    
    private Set<UUID> skillIds;
}

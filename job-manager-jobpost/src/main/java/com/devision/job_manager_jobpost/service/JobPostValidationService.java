package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;

import java.util.UUID;

/**
 * Service interface for job post validation operations.
 * Handles all validation logic for job posts.
 */
public interface JobPostValidationService {

    /**
     * Validate job post before creation
     */
    void validateJobPostForCreation(JobPost jobPost);

    /**
     * Validate job post before update
     */
    void validateJobPostForUpdate(UUID id, JobPost updatedJobPost);

    /**
     * Validate salary fields
     */
    void validateSalary(JobPost jobPost);

    /**
     * Validate expiry date
     */
    void validateExpiryDate(JobPost jobPost);

    /**
     * Validate that job post exists
     */
    void validateJobPostExists(UUID id);

    /**
     * Validate that company exists
     */
    void validateCompanyExists(UUID companyId);

    /**
     * Validate job post before publishing
     */
    void validateJobPostForPublishing(UUID id);

    /**
     * Validate skills before update
     */
    void validateSkills(UUID jobPostId, java.util.List<UUID> skillIds);
}

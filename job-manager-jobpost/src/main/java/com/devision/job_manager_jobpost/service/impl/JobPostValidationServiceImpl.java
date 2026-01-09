package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.service.CompanyIntegrationService;
import com.devision.job_manager_jobpost.service.JobPostValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of JobPostValidationService.
 * Handles all validation logic for job posts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostValidationServiceImpl implements JobPostValidationService {

    private final JobPostRepository jobPostRepository;
    private final CompanyIntegrationService companyIntegrationService;

    @Override
    public void validateJobPostForCreation(JobPost jobPost) {
        log.debug("Validating job post for creation");

        if (jobPost.getTitle() == null || jobPost.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job post title cannot be empty");
        }

        if (jobPost.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }

        validateCompanyExists(jobPost.getCompanyId());
        validateSalary(jobPost);
        validateExpiryDate(jobPost);

        log.debug("Job post validation for creation passed");
    }

    @Override
    public void validateJobPostForUpdate(UUID id, JobPost updatedJobPost) {
        log.debug("Validating job post for update: {}", id);

        validateJobPostExists(id);

        if (updatedJobPost.getTitle() != null && updatedJobPost.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job post title cannot be empty");
        }

        validateSalary(updatedJobPost);
        validateExpiryDate(updatedJobPost);

        log.debug("Job post validation for update passed");
    }

    @Override
    public void validateSalary(JobPost jobPost) {
        log.debug("Validating salary fields");

        if (jobPost.getSalaryMin() != null && jobPost.getSalaryMax() != null) {
            if (jobPost.getSalaryMin().compareTo(jobPost.getSalaryMax()) > 0) {
                throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
            }
        }

        if (jobPost.getSalaryMin() != null && jobPost.getSalaryMin().doubleValue() < 0) {
            throw new IllegalArgumentException("Minimum salary cannot be negative");
        }

        if (jobPost.getSalaryMax() != null && jobPost.getSalaryMax().doubleValue() < 0) {
            throw new IllegalArgumentException("Maximum salary cannot be negative");
        }

        log.debug("Salary validation passed");
    }

    @Override
    public void validateExpiryDate(JobPost jobPost) {
        log.debug("Validating expiry date");

        if (jobPost.getExpiryAt() != null) {
            if (jobPost.getExpiryAt().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Expiry date cannot be in the past");
            }
        }

        log.debug("Expiry date validation passed");
    }

    @Override
    public void validateJobPostExists(UUID id) {
        log.debug("Validating job post exists: {}", id);

        if (!jobPostRepository.existsById(id)) {
            throw new IllegalArgumentException("Job post not found with ID: " + id);
        }

        log.debug("Job post exists validation passed");
    }

    @Override
    public void validateCompanyExists(UUID companyId) {
        log.debug("Validating company exists: {}", companyId);

        if (!companyIntegrationService.companyExists(companyId)) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }

        log.debug("Company exists validation passed");
    }

    @Override
    public void validateJobPostForPublishing(UUID id) {
        log.debug("Validating job post for publishing: {}", id);

        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        if (jobPost.getTitle() == null || jobPost.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot publish job post without a title");
        }

        if (jobPost.getDescription() == null || jobPost.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot publish job post without a description");
        }

        if (jobPost.getExpiryAt() != null && jobPost.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot publish job post with past expiry date");
        }

        log.debug("Job post validation for publishing passed");
    }

    @Override
    public void validateSkills(UUID jobPostId, List<UUID> skillIds) {
        log.debug("Validating skills for job post: {}", jobPostId);

        validateJobPostExists(jobPostId);

        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("Skill IDs list cannot be null or empty");
        }

        // Check for duplicates
        long distinctCount = skillIds.stream().distinct().count();
        if (distinctCount != skillIds.size()) {
            throw new IllegalArgumentException("Duplicate skill IDs found in the list");
        }

        log.debug("Skills validation passed");
    }
}

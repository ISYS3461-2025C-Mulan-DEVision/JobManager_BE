package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;

import java.util.UUID;

/**
 * Service interface for job post write operations.
 * Handles creation, updates, and deletion of job posts.
 */
public interface JobPostWriteService {

    /**
     * Create a new job post
     */
    JobPost createJobPost(JobPost jobPost);

    /**
     * Update an existing job post
     */
    JobPost updateJobPost(UUID id, JobPost updatedJobPost);

    /**
     * Delete a job post by ID
     */
    void deleteJobPost(UUID id);

    /**
     * Delete all job posts by company ID
     */
    void deleteJobPostsByCompanyId(UUID companyId);
}

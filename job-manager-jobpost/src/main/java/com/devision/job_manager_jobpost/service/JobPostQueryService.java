package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for job post query operations.
 * Handles all read-only operations and data retrieval.
 */
public interface JobPostQueryService {

    /**
     * Get a job post by ID
     */
    Optional<JobPost> getJobPostById(UUID id);

    /**
     * Get a job post by ID with skills eagerly loaded
     */
    Optional<JobPost> getJobPostByIdWithSkills(UUID id);

    /**
     * Get a job post by ID with employment types eagerly loaded
     */
    Optional<JobPost> getJobPostByIdWithEmploymentTypes(UUID id);

    /**
     * Get all job posts by company ID with pagination
     */
    Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable);

    /**
     * Get all published job posts for a company with pagination
     */
    Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable);

    /**
     * Get all published job posts with pagination
     */
    Page<JobPost> getPublishedJobPosts(Pageable pageable);

    /**
     * Get all job posts with pagination
     */
    Page<JobPost> getAllJobPosts(Pageable pageable);

    /**
     * Search job posts by title with pagination
     */
    Page<JobPost> searchJobPostsByTitle(String title, Pageable pageable);

    /**
     * Search published job posts by title with pagination
     */
    Page<JobPost> searchPublishedJobPostsByTitle(String title, Pageable pageable);

    /**
     * Get job posts by location city with pagination
     */
    Page<JobPost> getJobPostsByCity(String city, Pageable pageable);

    /**
     * Get job posts by freshness flag with pagination
     */
    Page<JobPost> getJobPostsByFresher(boolean fresher, Pageable pageable);

    /**
     * Count published job posts by company ID
     */
    long countPublishedJobPostsByCompany(UUID companyId);

    /**
     * Get expired published job posts
     */
    List<JobPost> getExpiredPublishedJobPosts();

    /**
     * Check if job post exists by ID
     */
    boolean existsById(UUID id);
}

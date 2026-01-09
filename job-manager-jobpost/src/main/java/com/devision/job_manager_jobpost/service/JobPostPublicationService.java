package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service interface for job post publication operations.
 * Handles publishing, unpublishing, and expiry management.
 */
public interface JobPostPublicationService {

    /**
     * Publish a job post
     */
    JobPost publishJobPost(UUID id);

    /**
     * Unpublish a job post
     */
    JobPost unpublishJobPost(UUID id);

    /**
     * Update expiry date of a job post
     */
    JobPost updateExpiryDate(UUID id, LocalDateTime expiryAt);

    /**
     * Check if a job post is published
     */
    boolean isPublished(UUID id);

    /**
     * Check if a job post is expired
     */
    boolean isExpired(UUID id);

    /**
     * Expire all job posts that have passed their expiry date
     */
    void expireOutdatedJobPosts();
}

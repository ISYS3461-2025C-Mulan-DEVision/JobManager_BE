package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.model.JobPostSkill;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for job post skills management.
 * Handles all operations related to job post skills.
 */
public interface JobPostSkillsService {

    /**
     * Update job post skills and publish Kafka event for Ultimo 4.3.1
     * @param jobPostId The job post ID
     * @param newSkillIds The new list of skill IDs
     * @return Updated job post
     */
    JobPost updateJobPostSkills(UUID jobPostId, List<UUID> newSkillIds);

    /**
     * Add a skill to a job post
     */
    JobPost addSkillToJobPost(UUID jobPostId, UUID skillId);

    /**
     * Remove a skill from a job post
     */
    JobPost removeSkillFromJobPost(UUID jobPostId, UUID skillId);

    /**
     * Get all skills for a job post
     */
    List<JobPostSkill> getJobPostSkills(UUID jobPostId);

    /**
     * Check if a job post has a specific skill
     */
    boolean jobPostHasSkill(UUID jobPostId, UUID skillId);

    /**
     * Count skills for a job post
     */
    long countSkillsForJobPost(UUID jobPostId);

    /**
     * Clear all skills from a job post
     */
    void clearJobPostSkills(UUID jobPostId);
}

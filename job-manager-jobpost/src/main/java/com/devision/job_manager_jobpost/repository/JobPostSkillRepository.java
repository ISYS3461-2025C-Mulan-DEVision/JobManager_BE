package com.devision.job_manager_jobpost.repository;

import com.devision.job_manager_jobpost.model.JobPostSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing JobPostSkill relationships.
 * Handles operations related to job post skills.
 */
@Repository
public interface JobPostSkillRepository extends JpaRepository<JobPostSkill, UUID> {

    /**
     * Find all skills for a specific job post
     */
    @Query("SELECT jps FROM JobPostSkill jps WHERE jps.jobPost.jobPostId = :jobPostId")
    List<JobPostSkill> findByJobPostId(@Param("jobPostId") UUID jobPostId);

    /**
     * Find job post skills by skill ID
     */
    List<JobPostSkill> findBySkillId(UUID skillId);

    /**
     * Delete all skills for a specific job post
     */
    @Modifying
    @Query("DELETE FROM JobPostSkill jps WHERE jps.jobPost.jobPostId = :jobPostId")
    void deleteByJobPostId(@Param("jobPostId") UUID jobPostId);

    /**
     * Delete a specific skill from a job post
     */
    @Modifying
    @Query("DELETE FROM JobPostSkill jps WHERE jps.jobPost.jobPostId = :jobPostId AND jps.skillId = :skillId")
    void deleteByJobPostIdAndSkillId(@Param("jobPostId") UUID jobPostId, @Param("skillId") UUID skillId);

    /**
     * Check if a job post has a specific skill
     */
    @Query("SELECT COUNT(jps) > 0 FROM JobPostSkill jps WHERE jps.jobPost.jobPostId = :jobPostId AND jps.skillId = :skillId")
    boolean existsByJobPostIdAndSkillId(@Param("jobPostId") UUID jobPostId, @Param("skillId") UUID skillId);

    /**
     * Count skills for a job post
     */
    @Query("SELECT COUNT(jps) FROM JobPostSkill jps WHERE jps.jobPost.jobPostId = :jobPostId")
    long countByJobPostId(@Param("jobPostId") UUID jobPostId);
}

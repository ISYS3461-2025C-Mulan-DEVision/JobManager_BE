package com.devision.job_manager_jobpost.repository;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository for write operations on JobPost entities.
 * Handles all INSERT, UPDATE, and DELETE operations.
 */
@Repository
public interface JobPostWriteRepository extends JpaRepository<JobPost, UUID> {

    /**
     * Bulk update published status for job posts
     */
    @Modifying
    @Query("UPDATE JobPost j SET j.published = :published WHERE j.jobPostId = :id")
    int updatePublishedStatus(@Param("id") UUID id, @Param("published") boolean published);

    /**
     * Bulk update postedAt timestamp
     */
    @Modifying
    @Query("UPDATE JobPost j SET j.postedAt = :postedAt WHERE j.jobPostId = :id")
    int updatePostedAt(@Param("id") UUID id, @Param("postedAt") LocalDateTime postedAt);

    /**
     * Bulk update expiry date
     */
    @Modifying
    @Query("UPDATE JobPost j SET j.expiryAt = :expiryAt WHERE j.jobPostId = :id")
    int updateExpiryAt(@Param("id") UUID id, @Param("expiryAt") LocalDateTime expiryAt);

    /**
     * Delete all job posts by company ID
     */
    void deleteByCompanyId(UUID companyId);
}

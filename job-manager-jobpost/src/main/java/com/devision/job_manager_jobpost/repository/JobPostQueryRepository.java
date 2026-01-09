package com.devision.job_manager_jobpost.repository;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for read-only query operations on JobPost entities.
 * Handles all SELECT queries and data retrieval operations.
 */
@Repository
public interface JobPostQueryRepository extends JpaRepository<JobPost, UUID> {

    /**
     * Find all job posts by company ID with pagination
     */
    Page<JobPost> findByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Find all job posts by company ID without pagination
     */
    List<JobPost> findByCompanyId(UUID companyId);

    /**
     * Find all published job posts with pagination
     */
    Page<JobPost> findByPublishedTrue(Pageable pageable);

    /**
     * Find all published job posts for a specific company with pagination
     */
    Page<JobPost> findByPublishedTrueAndCompanyId(UUID companyId, Pageable pageable);

    /**
     * Find all published job posts that are not expired with pagination
     */
    Page<JobPost> findByPublishedTrueAndExpiryAtAfter(LocalDateTime now, Pageable pageable);

    /**
     * Count published job posts by company ID
     */
    long countByPublishedTrueAndCompanyId(UUID companyId);

    /**
     * Find job posts by location city with pagination
     */
    Page<JobPost> findByLocationCity(String city, Pageable pageable);

    /**
     * Find job posts by title (case-insensitive search)
     */
    @Query("SELECT j FROM JobPost j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<JobPost> searchByTitle(@Param("title") String title, Pageable pageable);

    /**
     * Find published job posts by title (case-insensitive search)
     */
    @Query("SELECT j FROM JobPost j WHERE j.published = true AND LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<JobPost> searchPublishedByTitle(@Param("title") String title, Pageable pageable);

    /**
     * Find job posts by freshness flag
     */
    Page<JobPost> findByPublishedTrueAndFresher(boolean fresher, Pageable pageable);

    /**
     * Find expired published job posts
     */
    @Query("SELECT j FROM JobPost j WHERE j.published = true AND j.expiryAt <= :now")
    List<JobPost> findExpiredPublishedJobPosts(@Param("now") LocalDateTime now);

    /**
     * Find job post by ID with skills eagerly loaded
     */
    @Query("SELECT j FROM JobPost j LEFT JOIN FETCH j.skills WHERE j.jobPostId = :id")
    Optional<JobPost> findByIdWithSkills(@Param("id") UUID id);

    /**
     * Find job post by ID with employment types eagerly loaded
     */
    @Query("SELECT j FROM JobPost j LEFT JOIN FETCH j.employmentTypes WHERE j.jobPostId = :id")
    Optional<JobPost> findByIdWithEmploymentTypes(@Param("id") UUID id);
}

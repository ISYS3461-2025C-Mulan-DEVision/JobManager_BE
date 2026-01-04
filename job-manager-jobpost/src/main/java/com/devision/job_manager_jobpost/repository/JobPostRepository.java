package com.devision.job_manager_jobpost.repository;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    Page<JobPost> findByCompanyId(UUID companyId, Pageable pageable);

    List<JobPost> findByCompanyId(UUID companyId);

    Page<JobPost> findByPublishedTrue(Pageable pageable);

    Page<JobPost> findByPublishedTrueAndCompanyId(UUID companyId, Pageable pageable);

    Page<JobPost> findByPublishedTrueAndExpiryAtAfter(LocalDateTime now, Pageable pageable);

    long countByPublishedTrueAndCompanyId(UUID companyId);
}

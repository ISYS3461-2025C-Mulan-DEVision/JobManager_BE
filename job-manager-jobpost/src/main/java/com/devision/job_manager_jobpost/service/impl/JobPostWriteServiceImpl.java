package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.service.JobPostWriteService;
import com.devision.job_manager_jobpost.service.internal.EventPublisherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of JobPostWriteService.
 * Handles all write operations for job posts (create, update, delete).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostWriteServiceImpl implements JobPostWriteService {

    private final JobPostRepository jobPostRepository;
    private final EventPublisherService eventPublisher;

    @Override
    @Transactional
    public JobPost createJobPost(JobPost jobPost) {
        log.info("Creating job post for companyId={}", jobPost.getCompanyId());

        // If not published, clear postedAt timestamp
        if (!jobPost.isPublished()) {
            jobPost.setPostedAt(null);
        }

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully created job post with ID: {}", savedJobPost.getJobPostId());

        return savedJobPost;
    }

    @Override
    @Transactional
    public JobPost updateJobPost(UUID id, JobPost updatedJobPost) {
        log.info("Updating job post with ID: {}", id);

        JobPost existing = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        // Update fields if they are not null
        if (updatedJobPost.getTitle() != null) {
            existing.setTitle(updatedJobPost.getTitle());
        }
        if (updatedJobPost.getDescription() != null) {
            existing.setDescription(updatedJobPost.getDescription());
        }
        if (updatedJobPost.getSalaryType() != null) {
            existing.setSalaryType(updatedJobPost.getSalaryType());
        }
        if (updatedJobPost.getSalaryMin() != null) {
            existing.setSalaryMin(updatedJobPost.getSalaryMin());
        }
        if (updatedJobPost.getSalaryMax() != null) {
            existing.setSalaryMax(updatedJobPost.getSalaryMax());
        }
        if (updatedJobPost.getSalaryNote() != null) {
            existing.setSalaryNote(updatedJobPost.getSalaryNote());
        }
        if (updatedJobPost.getLocationCity() != null) {
            existing.setLocationCity(updatedJobPost.getLocationCity());
        }

        // Update flags
        existing.setFresher(updatedJobPost.isFresher());

        if (updatedJobPost.getExpiryAt() != null) {
            existing.setExpiryAt(updatedJobPost.getExpiryAt());
        }

        JobPost savedJobPost = jobPostRepository.save(existing);
        log.info("Successfully updated job post with ID: {}", id);

        return savedJobPost;
    }

    @Override
    @Transactional
    public void deleteJobPost(UUID id) {
        log.info("Deleting job post with ID: {}", id);

        if (!jobPostRepository.existsById(id)) {
            throw new IllegalArgumentException("Job post not found with ID: " + id);
        }

        jobPostRepository.deleteById(id);
        log.info("Successfully deleted job post with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteJobPostsByCompanyId(UUID companyId) {
        log.info("Deleting all job posts for company ID: {}", companyId);

        jobPostRepository.findByCompanyId(companyId).forEach(jobPost -> {
            jobPostRepository.delete(jobPost);
            log.debug("Deleted job post ID: {} for company: {}", jobPost.getJobPostId(), companyId);
        });

        log.info("Successfully deleted all job posts for company ID: {}", companyId);
    }
}

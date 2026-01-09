package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.repository.JobPostQueryRepository;
import com.devision.job_manager_jobpost.service.JobPostPublicationService;
import com.devision.job_manager_jobpost.service.internal.EventPublisherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of JobPostPublicationService.
 * Handles publishing, unpublishing, and expiry management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostPublicationServiceImpl implements JobPostPublicationService {

    private final JobPostRepository jobPostRepository;
    private final JobPostQueryRepository jobPostQueryRepository;
    private final EventPublisherService eventPublisher;

    @Override
    @Transactional
    public JobPost publishJobPost(UUID id) {
        log.info("Publishing job post with ID: {}", id);

        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(true);
        jobPost.setPostedAt(LocalDateTime.now());

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully published job post with ID: {}", id);

        return savedJobPost;
    }

    @Override
    @Transactional
    public JobPost unpublishJobPost(UUID id) {
        log.info("Unpublishing job post with ID: {}", id);

        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(false);

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully unpublished job post with ID: {}", id);

        return savedJobPost;
    }

    @Override
    @Transactional
    public JobPost updateExpiryDate(UUID id, LocalDateTime expiryAt) {
        log.info("Updating expiry date for job post ID: {}", id);

        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setExpiryAt(expiryAt);

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully updated expiry date for job post ID: {}", id);

        return savedJobPost;
    }

    @Override
    public boolean isPublished(UUID id) {
        log.debug("Checking if job post is published: {}", id);

        return jobPostRepository.findById(id)
                .map(JobPost::isPublished)
                .orElse(false);
    }

    @Override
    public boolean isExpired(UUID id) {
        log.debug("Checking if job post is expired: {}", id);

        return jobPostRepository.findById(id)
                .map(jobPost -> {
                    if (jobPost.getExpiryAt() == null) {
                        return false;
                    }
                    return jobPost.getExpiryAt().isBefore(LocalDateTime.now());
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void expireOutdatedJobPosts() {
        log.info("Starting batch expiry of outdated job posts");

        List<JobPost> expiredPosts = jobPostQueryRepository.findExpiredPublishedJobPosts(LocalDateTime.now());

        log.info("Found {} expired job posts to unpublish", expiredPosts.size());

        expiredPosts.forEach(jobPost -> {
            jobPost.setPublished(false);
            jobPostRepository.save(jobPost);
            log.debug("Expired job post ID: {}", jobPost.getJobPostId());
        });

        log.info("Successfully expired {} job posts", expiredPosts.size());
    }
}

package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.service.JobPostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Override
    @Transactional
    public JobPost createJobPost(JobPost jobPost) {
        log.info("Creating job post for companyId={}", jobPost.getCompanyId());
        // Ensure new posts start unpublished unless explicitly set
        if (!jobPost.isPublished()) {
            jobPost.setPostedAt(null);
        }
        return jobPostRepository.save(jobPost);
    }

    @Override
    public Optional<JobPost> getJobPostById(Long id) {
        return jobPostRepository.findById(id);
    }

    @Override
    public Page<JobPost> getCompanyJobPosts(Long companyId, Pageable pageable) {
        return jobPostRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedCompanyJobPosts(Long companyId, Pageable pageable) {
        return jobPostRepository.findByPublishedTrueAndCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedJobPosts(Pageable pageable) {
        // Only return non-expired, published posts
        return jobPostRepository.findByPublishedTrueAndExpiryAtAfter(LocalDateTime.now(), pageable);
    }

    @Override
    @Transactional
    public JobPost updateJobPost(Long id, JobPost updatedJobPost) {
        JobPost existing = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

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
        if (updatedJobPost.getCountryId() != null) {
            existing.setCountryId(updatedJobPost.getCountryId());
        }
        // Flags and dates can be updated explicitly
        existing.setFresher(updatedJobPost.isFresher());
        if (updatedJobPost.getExpiryAt() != null) {
            existing.setExpiryAt(updatedJobPost.getExpiryAt());
        }

        return jobPostRepository.save(existing);
    }

    @Override
    @Transactional
    public JobPost publishJobPost(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(true);
        jobPost.setPostedAt(LocalDateTime.now());

        return jobPostRepository.save(jobPost);
    }

    @Override
    @Transactional
    public JobPost unpublishJobPost(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(false);

        return jobPostRepository.save(jobPost);
    }

    @Override
    @Transactional
    public void deleteJobPost(Long id) {
        if (!jobPostRepository.existsById(id)) {
            throw new IllegalArgumentException("Job post not found with ID: " + id);
        }
        jobPostRepository.deleteById(id);
    }
}



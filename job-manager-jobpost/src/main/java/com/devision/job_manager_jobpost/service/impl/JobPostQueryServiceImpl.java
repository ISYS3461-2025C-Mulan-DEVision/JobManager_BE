package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostQueryRepository;
import com.devision.job_manager_jobpost.service.JobPostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of JobPostQueryService.
 * Handles all read-only operations for job posts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostQueryServiceImpl implements JobPostQueryService {

    private final JobPostQueryRepository jobPostQueryRepository;

    @Override
    public Optional<JobPost> getJobPostById(UUID id) {
        log.debug("Fetching job post by ID: {}", id);
        return jobPostQueryRepository.findById(id);
    }

    @Override
    public Optional<JobPost> getJobPostByIdWithSkills(UUID id) {
        log.debug("Fetching job post by ID with skills: {}", id);
        return jobPostQueryRepository.findByIdWithSkills(id);
    }

    @Override
    public Optional<JobPost> getJobPostByIdWithEmploymentTypes(UUID id) {
        log.debug("Fetching job post by ID with employment types: {}", id);
        return jobPostQueryRepository.findByIdWithEmploymentTypes(id);
    }

    @Override
    public Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable) {
        log.debug("Fetching job posts for company: {}", companyId);
        return jobPostQueryRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable) {
        log.debug("Fetching published job posts for company: {}", companyId);
        return jobPostQueryRepository.findByPublishedTrueAndCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedJobPosts(Pageable pageable) {
        log.debug("Fetching all published job posts that are not expired");
        return jobPostQueryRepository.findByPublishedTrueAndExpiryAtAfter(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<JobPost> getAllJobPosts(Pageable pageable) {
        log.debug("Fetching all job posts");
        return jobPostQueryRepository.findAll(pageable);
    }

    @Override
    public Page<JobPost> searchJobPostsByTitle(String title, Pageable pageable) {
        log.debug("Searching job posts by title: {}", title);
        return jobPostQueryRepository.searchByTitle(title, pageable);
    }

    @Override
    public Page<JobPost> searchPublishedJobPostsByTitle(String title, Pageable pageable) {
        log.debug("Searching published job posts by title: {}", title);
        return jobPostQueryRepository.searchPublishedByTitle(title, pageable);
    }

    @Override
    public Page<JobPost> getJobPostsByCity(String city, Pageable pageable) {
        log.debug("Fetching job posts by city: {}", city);
        return jobPostQueryRepository.findByLocationCity(city, pageable);
    }

    @Override
    public Page<JobPost> getJobPostsByFresher(boolean fresher, Pageable pageable) {
        log.debug("Fetching job posts by fresher flag: {}", fresher);
        return jobPostQueryRepository.findByPublishedTrueAndFresher(fresher, pageable);
    }

    @Override
    public long countPublishedJobPostsByCompany(UUID companyId) {
        log.debug("Counting published job posts for company: {}", companyId);
        return jobPostQueryRepository.countByPublishedTrueAndCompanyId(companyId);
    }

    @Override
    public List<JobPost> getExpiredPublishedJobPosts() {
        log.debug("Fetching expired published job posts");
        return jobPostQueryRepository.findExpiredPublishedJobPosts(LocalDateTime.now());
    }

    @Override
    public boolean existsById(UUID id) {
        log.debug("Checking if job post exists: {}", id);
        return jobPostQueryRepository.existsById(id);
    }
}

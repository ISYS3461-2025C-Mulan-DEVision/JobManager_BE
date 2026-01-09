package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.service.JobPostPublicationService;
import com.devision.job_manager_jobpost.service.JobPostQueryService;
import com.devision.job_manager_jobpost.service.JobPostService;
import com.devision.job_manager_jobpost.service.JobPostSkillsService;
import com.devision.job_manager_jobpost.service.JobPostWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Facade implementation of JobPostService that delegates to specialized services.
 * This maintains backward compatibility while using the new modular service architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostServiceImpl implements JobPostService {

    private final JobPostQueryService jobPostQueryService;
    private final JobPostWriteService jobPostWriteService;
    private final JobPostPublicationService jobPostPublicationService;
    private final JobPostSkillsService jobPostSkillsService;

    @Override
    public JobPost createJobPost(JobPost jobPost) {
        log.debug("Delegating createJobPost to JobPostWriteService");
        return jobPostWriteService.createJobPost(jobPost);
    }

    @Override
    public Optional<JobPost> getJobPostById(UUID id) {
        log.debug("Delegating getJobPostById to JobPostQueryService");
        return jobPostQueryService.getJobPostById(id);
    }

    @Override
    public Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable) {
        log.debug("Delegating getCompanyJobPosts to JobPostQueryService");
        return jobPostQueryService.getCompanyJobPosts(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable) {
        log.debug("Delegating getPublishedCompanyJobPosts to JobPostQueryService");
        return jobPostQueryService.getPublishedCompanyJobPosts(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedJobPosts(Pageable pageable) {
        log.debug("Delegating getPublishedJobPosts to JobPostQueryService");
        return jobPostQueryService.getPublishedJobPosts(pageable);
    }

    @Override
    public Page<JobPost> getAllJobPosts(Pageable pageable) {
        log.debug("Delegating getAllJobPosts to JobPostQueryService");
        return jobPostQueryService.getAllJobPosts(pageable);
    }

    @Override
    public JobPost updateJobPost(UUID id, JobPost updatedJobPost) {
        log.debug("Delegating updateJobPost to JobPostWriteService");
        return jobPostWriteService.updateJobPost(id, updatedJobPost);
    }

    @Override
    public JobPost publishJobPost(UUID id) {
        log.debug("Delegating publishJobPost to JobPostPublicationService");
        return jobPostPublicationService.publishJobPost(id);
    }

    @Override
    public JobPost unpublishJobPost(UUID id) {
        log.debug("Delegating unpublishJobPost to JobPostPublicationService");
        return jobPostPublicationService.unpublishJobPost(id);
    }

    @Override
    public void deleteJobPost(UUID id) {
        log.debug("Delegating deleteJobPost to JobPostWriteService");
        jobPostWriteService.deleteJobPost(id);
    }

    /**
     * Update job post skills and publish Kafka event for Ultimo 4.3.1
     * CRITICAL: This enables instant notifications to matching applicants
     */
    @Override
    public JobPost updateJobPostSkills(UUID jobPostId, List<UUID> newSkillIds) {
        log.debug("Delegating updateJobPostSkills to JobPostSkillsService");
        return jobPostSkillsService.updateJobPostSkills(jobPostId, newSkillIds);
    }
}



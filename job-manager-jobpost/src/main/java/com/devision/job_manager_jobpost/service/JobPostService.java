package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface JobPostService {

    JobPost createJobPost(JobPost jobPost);

    Optional<JobPost> getJobPostById(UUID id);

    Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable);

    Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable);

    Page<JobPost> getPublishedJobPosts(Pageable pageable);

    JobPost updateJobPost(UUID id, JobPost updatedJobPost);

    JobPost publishJobPost(UUID id);

    JobPost unpublishJobPost(UUID id);

    void deleteJobPost(UUID id);
}



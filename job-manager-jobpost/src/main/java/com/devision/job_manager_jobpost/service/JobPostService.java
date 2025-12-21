package com.devision.job_manager_jobpost.service;

import com.devision.job_manager_jobpost.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface JobPostService {

    JobPost createJobPost(JobPost jobPost);

    Optional<JobPost> getJobPostById(Long id);

    Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable);

    Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable);

    Page<JobPost> getPublishedJobPosts(Pageable pageable);

    JobPost updateJobPost(Long id, JobPost updatedJobPost);

    JobPost publishJobPost(Long id);

    JobPost unpublishJobPost(Long id);

    void deleteJobPost(Long id);
}



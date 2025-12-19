package com.devision.job_manager_jobpost.controller.external;

import com.devision.job_manager_jobpost.api.external.JobPostExternalApi;
import com.devision.job_manager_jobpost.dto.external.JobPostBasicInfoDto;
import com.devision.job_manager_jobpost.dto.external.JobPostStatusDto;
import com.devision.job_manager_jobpost.dto.external.JobPostSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * External API controller for other microservices
 * Accessible via: /api/external/job-posts
 */
@RestController
@RequestMapping("/api/external/job-posts")
@RequiredArgsConstructor
@Slf4j
public class JobPostExternalController {
    
    private final JobPostExternalApi jobPostExternalApi;
    
    @GetMapping("/{id}")
    public ResponseEntity<JobPostBasicInfoDto> getJobPostBasicInfo(@PathVariable Long id) {
        log.info("External request: Get job post basic info for ID: {}", id);
        return jobPostExternalApi.getJobPostBasicInfo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<JobPostStatusDto> getJobPostStatus(@PathVariable Long id) {
        log.info("External request: Get job post status for ID: {}", id);
        return jobPostExternalApi.getJobPostStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobPostSummaryDto>> getPublishedJobPostsByCompany(
            @PathVariable Long companyId) {
        log.info("External request: Get published job posts for company ID: {}", companyId);
        Page<JobPostSummaryDto> jobPosts = jobPostExternalApi.getPublishedJobPostsByCompany(companyId, pageable);
        return ResponseEntity.ok(jobPosts);
    }
    
    @GetMapping("/{id}/is-published")
    public ResponseEntity<Boolean> isJobPostPublished(@PathVariable Long id) {
        log.info("External request: Check if job post is published: {}", id);
        return ResponseEntity.ok(jobPostExternalApi.isJobPostPublished(id));
    }
    
    @GetMapping("/{id}/is-expired")
    public ResponseEntity<Boolean> isJobPostExpired(@PathVariable Long id) {
        log.info("External request: Check if job post is expired: {}", id);
        return ResponseEntity.ok(jobPostExternalApi.isJobPostExpired(id));
    }
    
    @GetMapping("/company/{companyId}/count")
    public ResponseEntity<Long> getPublishedJobPostCount(@PathVariable Long companyId) {
        log.info("External request: Get published job post count for company ID: {}", companyId);
        return ResponseEntity.ok(jobPostExternalApi.getPublishedJobPostCount(companyId));
    }
}
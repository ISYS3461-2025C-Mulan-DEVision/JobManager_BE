package com.devision.job_manager_jobpost.api.external.impl;

import com.devision.job_manager_jobpost.api.external.JobPostExternalApi;
import com.devision.job_manager_jobpost.dto.external.JobPostBasicInfoDto;
import com.devision.job_manager_jobpost.dto.external.JobPostStatusDto;
import com.devision.job_manager_jobpost.dto.external.JobPostSummaryDto;
import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostExternalApiImpl implements JobPostExternalApi {
    
    private final JobPostRepository jobPostRepository;
    
    @Override
    public Optional<JobPostBasicInfoDto> getJobPostBasicInfo(UUID jobPostId) {
        log.debug("External API: Getting basic info for job post ID: {}", jobPostId);
        return jobPostRepository.findById(jobPostId)
                .map(this::mapToBasicInfo);
    }
    
    @Override
    public Optional<JobPostStatusDto> getJobPostStatus(UUID jobPostId) {
        log.debug("External API: Getting status for job post ID: {}", jobPostId);
        return jobPostRepository.findById(jobPostId)
                .map(jobPost -> JobPostStatusDto.builder()
                        .id(jobPost.getJobPostId())
                        .companyId(jobPost.getCompanyId())
                        .isPublished(jobPost.isPublished())
                        .isExpired(isExpired(jobPost))
                        .isActive(jobPost.isPublished() && !isExpired(jobPost))
                        .build());
    }

    @Override public Optional<JobPostSummaryDto> getJobPostSummary(UUID jobPostId) {
        log.debug("External API: Getting summary for job post ID: {}", jobPostId);
        return jobPostRepository.findById(jobPostId)
                .map(this::mapToSummary);
    }
    
    @Override

    public Optional<Page<JobPostSummaryDto>> getPublishedJobPostsByCompany(UUID companyId, Pageable pageable) {
    Page<JobPost> page = jobPostRepository.findByPublishedTrueAndCompanyId(companyId, pageable);
    Page<JobPostSummaryDto> dtoPage = page.map(this::mapToSummary);
     return Optional.ofNullable(dtoPage);
    }
    
    @Override
    public boolean isJobPostPublished(UUID jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .map(JobPost::isPublished)
                .orElse(false);
    }
    
    @Override
    public boolean isJobPostExpired(UUID jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .map(this::isExpired)
                .orElse(true);
    }
    
    @Override
    public long getPublishedJobPostCount(UUID companyId) {
        return jobPostRepository.countByPublishedTrueAndCompanyId(companyId);
    }
    
    // Helper methods
    private JobPostBasicInfoDto mapToBasicInfo(JobPost jobPost) {
        return JobPostBasicInfoDto.builder()
                .id(jobPost.getJobPostId())
                .companyId(jobPost.getCompanyId())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .isPublished(jobPost.isPublished())
                .isFresher(jobPost.isFresher())
                .locationCity(jobPost.getLocationCity())
                .build();
    }
    
    private JobPostSummaryDto mapToSummary(JobPost jobPost) {
        return JobPostSummaryDto.builder()
                .id(jobPost.getJobPostId())
                .companyId(jobPost.getCompanyId())
                .title(jobPost.getTitle())
                .locationCity(jobPost.getLocationCity())
                // .countryId(jobPost.getCountryId())
                .isFresher(jobPost.isFresher())
                .postedAt(jobPost.getPostedAt())
                .salary(JobPostSummaryDto.SalaryInfoDto.builder()
                        .type(jobPost.getSalaryType() != null ? jobPost.getSalaryType().name() : null)
                        .min(jobPost.getSalaryMin())
                        .max(jobPost.getSalaryMax())
                        .note(jobPost.getSalaryNote())
                        .build())
                .build();
    }
    
    private boolean isExpired(JobPost jobPost) {
        return jobPost.getExpiryAt() != null && 
               jobPost.getExpiryAt().isBefore(LocalDateTime.now());
    }
}
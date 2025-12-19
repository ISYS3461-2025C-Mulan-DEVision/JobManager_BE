package com.devision.job_manager_jobpost.api.external;

import com.devision.job_manager_jobpost.dto.external.JobPostBasicInfoDto;
import com.devision.job_manager_jobpost.dto.external.JobPostStatusDto;
import com.devision.job_manager_jobpost.dto.external.JobPostSummaryDto;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface JobPostExternalApi {
    
    Optional<JobPostBasicInfoDto> getJobPostBasicInfo(Long id);

    Optional<JobPostStatusDto> getJobPostStatus(Long id);

    Optional<JobPostSummaryDto> getJobPostSummary(Long id);

    Optional<Page<JobPostSummaryDto>> getPublishedJobPostsByCompany(Long companyId, Pageable pageable);

    
    boolean isJobPostPublished(Long id);

    boolean isJobPostExpired(Long id);

    long getPublishedJobPostCount(Long companyId);
}
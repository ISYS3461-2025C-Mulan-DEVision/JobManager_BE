package com.devision.job_manager_jobpost.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostStatusDto {
    private Long id;
    private Long companyId;
    private boolean isPublished;
    private boolean isExpired;
    private boolean isActive;
}
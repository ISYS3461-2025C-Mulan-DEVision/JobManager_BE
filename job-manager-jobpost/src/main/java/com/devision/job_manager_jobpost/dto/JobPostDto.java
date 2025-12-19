package com.devision.job_manager_jobpost.dto;

import com.devision.job_manager_jobpost.model.SalaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostDto {
    private Long id;
    private Long companyId;
    private String title;
    private String description;
    private boolean fresher;
    private SalaryType salaryType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryNote;
    private String locationCity;
    private String countryCode; // can be derived on client if needed
    private boolean published;
    private boolean aPrivate;
    private LocalDateTime postedAt;
    private LocalDateTime expiryAt;
}



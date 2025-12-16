package com.devision.job_manager_jobpost.dto;

import com.devision.job_manager_jobpost.model.SalaryType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobPostRequest {

    @Size(max = 255)
    private String title;

    private String description;

    private SalaryType salaryType;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    @Size(max = 255)
    private String salaryNote;

    @Size(max = 128)
    private String locationCity;

    private UUID countryId;

    private Boolean fresher;

    private Boolean aPrivate;

    private LocalDateTime expiryAt;
}



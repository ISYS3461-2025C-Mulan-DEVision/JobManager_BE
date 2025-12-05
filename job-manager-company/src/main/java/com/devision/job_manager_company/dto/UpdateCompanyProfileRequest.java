package com.devision.job_manager_company.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyProfileRequest {
    private String aboutUs;
    
    private String whoWeSeek;
    
    @Size(max = 512, message = "Logo URL must be less than 512 characters")
    private String logoUrl;
    
    @Size(max = 512, message = "Website URL must be less than 512 characters")
    private String websiteUrl;
    
    @Size(max = 512, message = "LinkedIn URL must be less than 512 characters")
    private String linkedinUrl;
    
    @Size(max = 128, message = "Industry must be less than 128 characters")
    private String industry;
    
    @Size(max = 64, message = "Company size must be less than 64 characters")
    private String companySize;
    
    private Integer foundedYear;
}

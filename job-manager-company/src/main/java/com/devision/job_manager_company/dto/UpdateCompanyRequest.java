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
public class UpdateCompanyRequest {
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 32, message = "Phone must be less than 32 characters")
    private String phone;
    
    @Size(max = 255, message = "Street address must be less than 255 characters")
    private String streetAddress;
    
    @Size(max = 128, message = "City must be less than 128 characters")
    private String city;
    
    @Size(max = 3, message = "Country code must be less than 3 characters")
    private String countryCode;
}

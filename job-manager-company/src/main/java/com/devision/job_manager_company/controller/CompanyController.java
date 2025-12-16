package com.devision.job_manager_company.controller;

import com.devision.job_manager_company.dto.*;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;
import com.devision.job_manager_company.security.SecurityUtils;
import com.devision.job_manager_company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyDto>> getCompany(@PathVariable UUID id) {
        log.info("Getting company with ID: {}", id);
        
        // Verify the authenticated company can access this resource
        securityUtils.verifyCompanyAccess(id);
        
        return companyService.getCompanyById(id)
                .map(company -> {
                    CompanyDto dto = mapToDto(company);
                    return ResponseEntity.ok(ApiResponse.success("Company found", dto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getCompanyProfile(@PathVariable UUID id) {
        log.info("Getting company profile for ID: {}", id);
        
        // Verify the authenticated company can access this resource
        securityUtils.verifyCompanyAccess(id);
        
        return companyService.getCompanyWithProfile(id)
                .map(company -> {
                    CompanyProfileDto dto = mapProfileToDto(company.getProfile());
                    return ResponseEntity.ok(ApiResponse.success("Company profile found", dto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyDto>> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        log.info("Updating company with ID: {}", id);
        
        // Verify the authenticated company can modify this resource
        securityUtils.verifyCompanyAccess(id);
        
        try {
            Company updatedCompany = Company.builder()
                    .name(request.getName())
                    .phone(request.getPhone())
                    .streetAddress(request.getStreetAddress())
                    .city(request.getCity())
                    .countryCode(request.getCountryCode())
                    .build();
            
            Company company = companyService.updateCompany(id, updatedCompany);
            CompanyDto dto = mapToDto(company);
            return ResponseEntity.ok(ApiResponse.success("Company updated successfully", dto));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update company: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> updateCompanyProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyProfileRequest request) {
        log.info("Updating company profile for ID: {}", id);
        
        // Verify the authenticated company can modify this resource
        securityUtils.verifyCompanyAccess(id);
        
        try {
            CompanyProfile updatedProfile = CompanyProfile.builder()
                    .aboutUs(request.getAboutUs())
                    .whoWeSeek(request.getWhoWeSeek())
                    .websiteUrl(request.getWebsiteUrl())
                    .linkedinUrl(request.getLinkedinUrl())
                    .industry(request.getIndustry())
                    .companySize(request.getCompanySize())
                    .foundedYear(request.getFoundedYear())
                    .build();
            
            CompanyProfile profile = companyService.updateCompanyProfile(id, updatedProfile);
            CompanyProfileDto dto = mapProfileToDto(profile);
            return ResponseEntity.ok(ApiResponse.success("Company profile updated successfully", dto));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update company profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Company Service is running");
    }

    // Exception handler for AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
    }

    private CompanyDto mapToDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .phone(company.getPhone())
                .streetAddress(company.getStreetAddress())
                .city(company.getCity())
                .countryCode(company.getCountryCode())
                .build();
    }

    private CompanyProfileDto mapProfileToDto(CompanyProfile profile) {
        if (profile == null) {
            return null;
        }
        return CompanyProfileDto.builder()
                .companyId(profile.getCompanyId())
                .aboutUs(profile.getAboutUs())
                .whoWeSeek(profile.getWhoWeSeek())
                .logoUrl(profile.getLogoUrl())
                .bannerUrl(profile.getBannerUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .industry(profile.getIndustry())
                .companySize(profile.getCompanySize())
                .foundedYear(profile.getFoundedYear())
                .build();
    }
}

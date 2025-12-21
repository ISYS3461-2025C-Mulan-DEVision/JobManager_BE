package com.devision.job_manager_company.controller;

import com.devision.job_manager_company.dto.*;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;
import com.devision.job_manager_company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyDto>> getCompany(@PathVariable UUID id) {
        log.info("Getting company with ID: {}", id);
        
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

    @GetMapping("/dial-codes")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getDialCodes() {
        log.info("Getting dial codes list");
        
        List<Map<String, String>> dialCodes = new ArrayList<>();
        
        // Add all dial codes with their country names
        dialCodes.add(Map.of("code", "1", "name", "USA/Canada"));
        dialCodes.add(Map.of("code", "7", "name", "Russia"));
        dialCodes.add(Map.of("code", "20", "name", "Egypt"));
        dialCodes.add(Map.of("code", "27", "name", "South Africa"));
        dialCodes.add(Map.of("code", "30", "name", "Greece"));
        dialCodes.add(Map.of("code", "31", "name", "Netherlands"));
        dialCodes.add(Map.of("code", "32", "name", "Belgium"));
        dialCodes.add(Map.of("code", "33", "name", "France"));
        dialCodes.add(Map.of("code", "34", "name", "Spain"));
        dialCodes.add(Map.of("code", "36", "name", "Hungary"));
        dialCodes.add(Map.of("code", "39", "name", "Italy"));
        dialCodes.add(Map.of("code", "40", "name", "Romania"));
        dialCodes.add(Map.of("code", "41", "name", "Switzerland"));
        dialCodes.add(Map.of("code", "43", "name", "Austria"));
        dialCodes.add(Map.of("code", "44", "name", "UK"));
        dialCodes.add(Map.of("code", "45", "name", "Denmark"));
        dialCodes.add(Map.of("code", "46", "name", "Sweden"));
        dialCodes.add(Map.of("code", "47", "name", "Norway"));
        dialCodes.add(Map.of("code", "48", "name", "Poland"));
        dialCodes.add(Map.of("code", "49", "name", "Germany"));
        dialCodes.add(Map.of("code", "54", "name", "Argentina"));
        dialCodes.add(Map.of("code", "55", "name", "Brazil"));
        dialCodes.add(Map.of("code", "56", "name", "Chile"));
        dialCodes.add(Map.of("code", "57", "name", "Colombia"));
        dialCodes.add(Map.of("code", "58", "name", "Venezuela"));
        dialCodes.add(Map.of("code", "60", "name", "Malaysia"));
        dialCodes.add(Map.of("code", "61", "name", "Australia"));
        dialCodes.add(Map.of("code", "62", "name", "Indonesia"));
        dialCodes.add(Map.of("code", "63", "name", "Philippines"));
        dialCodes.add(Map.of("code", "65", "name", "Singapore"));
        dialCodes.add(Map.of("code", "66", "name", "Thailand"));
        dialCodes.add(Map.of("code", "81", "name", "Japan"));
        dialCodes.add(Map.of("code", "82", "name", "South Korea"));
        dialCodes.add(Map.of("code", "84", "name", "Vietnam"));
        dialCodes.add(Map.of("code", "86", "name", "China"));
        dialCodes.add(Map.of("code", "90", "name", "Turkey"));
        dialCodes.add(Map.of("code", "91", "name", "India"));
        dialCodes.add(Map.of("code", "92", "name", "Pakistan"));
        dialCodes.add(Map.of("code", "93", "name", "Afghanistan"));
        dialCodes.add(Map.of("code", "94", "name", "Sri Lanka"));
        dialCodes.add(Map.of("code", "95", "name", "Myanmar"));
        dialCodes.add(Map.of("code", "98", "name", "Iran"));
        dialCodes.add(Map.of("code", "234", "name", "Nigeria"));
        dialCodes.add(Map.of("code", "254", "name", "Kenya"));
        dialCodes.add(Map.of("code", "375", "name", "Belarus"));
        dialCodes.add(Map.of("code", "380", "name", "Ukraine"));
        dialCodes.add(Map.of("code", "852", "name", "Hong Kong"));
        dialCodes.add(Map.of("code", "853", "name", "Macau"));
        dialCodes.add(Map.of("code", "886", "name", "Taiwan"));
        dialCodes.add(Map.of("code", "966", "name", "Saudi Arabia"));
        dialCodes.add(Map.of("code", "971", "name", "UAE"));
        dialCodes.add(Map.of("code", "972", "name", "Israel"));
        dialCodes.add(Map.of("code", "973", "name", "Bahrain"));
        dialCodes.add(Map.of("code", "974", "name", "Qatar"));
        
        return ResponseEntity.ok(ApiResponse.success("Dial codes list", dialCodes));
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

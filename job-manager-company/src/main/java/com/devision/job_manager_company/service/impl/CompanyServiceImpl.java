package com.devision.job_manager_company.service.impl;

import com.devision.job_manager_company.event.CompanyRegisteredEvent;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;
import com.devision.job_manager_company.repository.CompanyProfileRepository;
import com.devision.job_manager_company.repository.CompanyRepository;
import com.devision.job_manager_company.service.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository companyProfileRepository;

    @Override
    @Transactional
    public Company createCompanyFromEvent(CompanyRegisteredEvent event) {
        log.info("Creating company from registration event for ID: {}", event.getCompanyId());

        // Check if company already exists (idempotency)
        if (companyRepository.existsById(event.getCompanyId())) {
            log.warn("Company already exists with ID: {}", event.getCompanyId());
            return companyRepository.findById(event.getCompanyId()).orElseThrow();
        }

        // Create company entity
        Company company = Company.builder()
                .id(event.getCompanyId())
                .name(event.getName() != null ? event.getName() : "")
                .phone(event.getPhone())
                .streetAddress(event.getStreetAddress())
                .city(event.getCity())
                .countryCode(event.getCountryCode() != null ? event.getCountryCode() : "XX")
                .build();

        company = companyRepository.save(company);
        log.info("Company created with ID: {}", company.getId());

        // Create empty company profile
        CompanyProfile profile = CompanyProfile.builder()
                .company(company)
                .build();

        companyProfileRepository.save(profile);
        log.info("Company profile created for company ID: {}", company.getId());

        return company;
    }

    @Override
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Company> getCompanyWithProfile(Long id) {
        return companyRepository.findByIdWithProfile(id);
    }

    @Override
    @Transactional
    public Company updateCompany(Long id, Company updatedCompany) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));

        if (updatedCompany.getName() != null) {
            company.setName(updatedCompany.getName());
        }
        if (updatedCompany.getPhone() != null) {
            company.setPhone(updatedCompany.getPhone());
        }
        if (updatedCompany.getStreetAddress() != null) {
            company.setStreetAddress(updatedCompany.getStreetAddress());
        }
        if (updatedCompany.getCity() != null) {
            company.setCity(updatedCompany.getCity());
        }
        if (updatedCompany.getCountryCode() != null) {
            company.setCountryCode(updatedCompany.getCountryCode());
        }

        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public CompanyProfile updateCompanyProfile(Long companyId, CompanyProfile updatedProfile) {
        CompanyProfile profile = companyProfileRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for company ID: " + companyId));

        if (updatedProfile.getAboutUs() != null) {
            profile.setAboutUs(updatedProfile.getAboutUs());
        }
        if (updatedProfile.getWhoWeSeek() != null) {
            profile.setWhoWeSeek(updatedProfile.getWhoWeSeek());
        }
        if (updatedProfile.getWebsiteUrl() != null) {
            profile.setWebsiteUrl(updatedProfile.getWebsiteUrl());
        }
        if (updatedProfile.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(updatedProfile.getLinkedinUrl());
        }
        if (updatedProfile.getIndustry() != null) {
            profile.setIndustry(updatedProfile.getIndustry());
        }
        if (updatedProfile.getCompanySize() != null) {
            profile.setCompanySize(updatedProfile.getCompanySize());
        }
        if (updatedProfile.getFoundedYear() != null) {
            profile.setFoundedYear(updatedProfile.getFoundedYear());
        }

        return companyProfileRepository.save(profile);
    }
}

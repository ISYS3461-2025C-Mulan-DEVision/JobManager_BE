package com.devision.job_manager_auth.service.external.impl;

import com.devision.job_manager_auth.dto.external.CompanyAuthStatusDto;
import com.devision.job_manager_auth.dto.external.CompanyBasicInfoDto;
import com.devision.job_manager_auth.entity.Company;
import com.devision.job_manager_auth.entity.SsoProvider;
import com.devision.job_manager_auth.repository.CompanyRepository;
import com.devision.job_manager_auth.service.external.CompanyExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyExternalServiceImpl implements CompanyExternalService {
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyBasicInfoDto> getCompanyBasicInfo(Long companyId) {
        log.info("External API: Get company basic info for ID: {}", companyId);

        return companyRepository.findById(companyId)
                .map(this::mapToBasicInfoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyBasicInfoDto> getCompanyBasicInfoByEmail(String email) {
        log.info("External API: Get company basic info for email: {}", email);

        return companyRepository.findByEmail(email)
                .map(this::mapToBasicInfoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyAuthStatusDto> getCompanyAuthStatus(Long companyId) {
        log.info("External API: Get auth status for company ID: {}", companyId);

        return companyRepository.findById(companyId)
                .map(this::mapToAuthStatusDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCompanyActivated(String email) {
        log.info("External API: Check if company activated: {}", email);

        return companyRepository.findByEmail(email)
                .map(Company::getIsActivated)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCompanyLocked(String email) {
        log.info("External API: Check if company locked: {}", email);

        return companyRepository.findByEmail(email)
                .map(Company::getIsLocked)
                .orElse(false);
    }

    /**
     * PRIVATE MAPS
     */

    private CompanyBasicInfoDto mapToBasicInfoDto(Company company) {
        return CompanyBasicInfoDto.builder()
                .id(company.getId())
                .email(company.getEmail())
                .companyName(company.getName())
                .country(company.getCountry())
                .city(company.getCity())
                .role(company.getRole())
                .isActivated(company.getIsActivated())
                .createdAt(company.getCreatedAt())
                .build();
    }

    private CompanyAuthStatusDto mapToAuthStatusDto(Company company) {
        return CompanyAuthStatusDto.builder()
                .companyId(company.getId())
                .email(company.getEmail())
                .isActivated(company.getIsActivated())
                .isLocked(company.getIsLocked())
                .isSsoUser(company.getSsoProvider() != SsoProvider.NONE)
                .build();
    }

}

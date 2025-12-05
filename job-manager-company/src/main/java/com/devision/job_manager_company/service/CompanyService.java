package com.devision.job_manager_company.service;

import com.devision.job_manager_company.event.CompanyRegisteredEvent;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;

import java.util.Optional;

/**
 * Service for managing company profiles.
 */
public interface CompanyService {

    /**
     * Create a new company from registration event.
     */
    Company createCompanyFromEvent(CompanyRegisteredEvent event);

    /**
     * Get company by ID.
     */
    Optional<Company> getCompanyById(Long id);

    /**
     * Get company with profile.
     */
    Optional<Company> getCompanyWithProfile(Long id);

    /**
     * Update company basic info.
     */
    Company updateCompany(Long id, Company company);

    /**
     * Update company profile.
     */
    CompanyProfile updateCompanyProfile(Long companyId, CompanyProfile profile);
}

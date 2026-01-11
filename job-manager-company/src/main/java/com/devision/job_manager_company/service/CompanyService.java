package com.devision.job_manager_company.service;

import com.devision.job_manager_company.event.CompanyRegisteredEvent;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    Company createCompanyFromEvent(CompanyRegisteredEvent event);

    Optional<Company> getCompanyById(UUID id);

    Optional<Company> getCompanyWithProfile(UUID id);

    Company updateCompany(UUID id, Company company);

    CompanyProfile updateCompanyProfile(UUID companyId, CompanyProfile profile);

    void updateProfileLogoUrl(UUID companyId, String logoUrl);

    void updateProfileBannerUrl(UUID companyId, String bannerUrl);

    Optional<List<CompanyProfile>> getAllCompanies();
}

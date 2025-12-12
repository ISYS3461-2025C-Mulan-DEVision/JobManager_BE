package com.devision.job_manager_company.service;

import com.devision.job_manager_company.event.CompanyRegisteredEvent;
import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyProfile;

import java.util.Optional;

public interface CompanyService {

    Company createCompanyFromEvent(CompanyRegisteredEvent event);

    Optional<Company> getCompanyById(Long id);

    Optional<Company> getCompanyWithProfile(Long id);

    Company updateCompany(Long id, Company company);

    CompanyProfile updateCompanyProfile(Long companyId, CompanyProfile profile);
}

package com.devision.job_manager_company.repository;

import com.devision.job_manager_company.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    // Find profile by company ID
    Optional<CompanyProfile> findByCompanyId(Long companyId);

    // Check if profile exists for company
    boolean existsByCompanyId(Long companyId);
}

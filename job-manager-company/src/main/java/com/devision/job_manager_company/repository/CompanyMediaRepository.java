package com.devision.job_manager_company.repository;

import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyMediaRepository extends JpaRepository<CompanyMedia, Long> {

    // Find all media for a company
    List<CompanyMedia> findByCompanyIdOrderByDisplayOrderAsc(Long companyId);

    // Find media by company and type
    List<CompanyMedia> findByCompanyIdAndTypeOrderByDisplayOrderAsc(Long companyId, MediaType type);

    // Delete all media for a company
    void deleteByCompanyId(Long companyId);

    // Count media for a company
    long countByCompanyId(Long companyId);
}

package com.devision.job_manager_company.repository;

import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    // Find media by ID and company ID (for validation)
    @Query("SELECT m FROM CompanyMedia m WHERE m.id = :mediaId AND m.company.id = :companyId")
    Optional<CompanyMedia> findByIdAndCompanyId(@Param("mediaId") Long mediaId, @Param("companyId") Long companyId);

    // Find all media IDs for a company (for bulk reorder validation)
    @Query("SELECT m.id FROM CompanyMedia m WHERE m.company.id = :companyId ORDER BY m.displayOrder ASC")
    List<Long> findIdsByCompanyId(@Param("companyId") Long companyId);

    // Update display order for a specific media
    @Modifying
    @Query("UPDATE CompanyMedia m SET m.displayOrder = :displayOrder WHERE m.company.id = :companyId AND m.id = :mediaId")
    void updateDisplayOrder(@Param("companyId") Long companyId, @Param("mediaId") Long mediaId, @Param("displayOrder") int displayOrder);
}

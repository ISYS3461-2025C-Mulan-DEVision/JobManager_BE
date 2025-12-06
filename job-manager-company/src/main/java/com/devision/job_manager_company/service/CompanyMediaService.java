package com.devision.job_manager_company.service;

import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CompanyMediaService {

    CompanyMedia uploadLogo(Long companyId, MultipartFile file) throws IOException;

    CompanyMedia uploadBanner(Long companyId, MultipartFile file) throws IOException;

    CompanyMedia uploadMedia(Long companyId, MediaType type, MultipartFile file, String title, String description) throws IOException;

    List<CompanyMedia> getCompanyMedia(Long companyId);

    List<CompanyMedia> getCompanyMediaByType(Long companyId, MediaType type);

    void deleteMedia(Long mediaId);

    void updateDisplayOrder(Long mediaId, Integer displayOrder);
}

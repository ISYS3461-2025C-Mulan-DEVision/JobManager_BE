package com.devision.job_manager_company.service.impl;

import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import com.devision.job_manager_company.repository.CompanyMediaRepository;
import com.devision.job_manager_company.repository.CompanyRepository;
import com.devision.job_manager_company.service.CompanyMediaService;
import com.devision.job_manager_company.service.MediaStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyMediaServiceImpl implements CompanyMediaService {

    private final CompanyMediaRepository companyMediaRepository;
    private final CompanyRepository companyRepository;
    private final MediaStorageService mediaStorageService;

    @Override
    @Transactional
    public CompanyMedia uploadLogo(Long companyId, MultipartFile file) throws IOException {
        log.info("Uploading logo for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Upload to Firebase Storage
        String url = mediaStorageService.uploadCompanyLogo(companyId, file);

        // Delete old logo if exists
        List<CompanyMedia> existingLogos = companyMediaRepository
                .findByCompanyIdAndTypeOrderByDisplayOrderAsc(companyId, MediaType.BANNER);
        
        for (CompanyMedia oldLogo : existingLogos) {
            mediaStorageService.deleteFile(oldLogo.getUrl());
            companyMediaRepository.delete(oldLogo);
        }

        // Save new logo
        CompanyMedia logo = CompanyMedia.builder()
                .company(company)
                .type(MediaType.IMAGE)
                .url(url)
                .title("Company Logo")
                .displayOrder(0)
                .build();

        return companyMediaRepository.save(logo);
    }

    @Override
    @Transactional
    public CompanyMedia uploadBanner(Long companyId, MultipartFile file) throws IOException {
        log.info("Uploading banner for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Upload to Firebase Storage
        String url = mediaStorageService.uploadCompanyBanner(companyId, file);

        // Delete old banner if exists
        List<CompanyMedia> existingBanners = companyMediaRepository
                .findByCompanyIdAndTypeOrderByDisplayOrderAsc(companyId, MediaType.BANNER);
        
        for (CompanyMedia oldBanner : existingBanners) {
            mediaStorageService.deleteFile(oldBanner.getUrl());
            companyMediaRepository.delete(oldBanner);
        }

        // Save new banner
        CompanyMedia banner = CompanyMedia.builder()
                .company(company)
                .type(MediaType.BANNER)
                .url(url)
                .title("Company Banner")
                .displayOrder(0)
                .build();

        return companyMediaRepository.save(banner);
    }

    @Override
    @Transactional
    public CompanyMedia uploadMedia(Long companyId, MediaType type, MultipartFile file, 
                                   String title, String description) throws IOException {
        log.info("Uploading media for company ID: {}, type: {}", companyId, type);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Upload to Firebase Storage
        String url = mediaStorageService.uploadCompanyMedia(companyId, file);

        // Get next display order
        long count = companyMediaRepository.countByCompanyId(companyId);

        // Save media
        CompanyMedia media = CompanyMedia.builder()
                .company(company)
                .type(type)
                .url(url)
                .title(title)
                .description(description)
                .displayOrder((int) count)
                .build();

        return companyMediaRepository.save(media);
    }

    @Override
    public List<CompanyMedia> getCompanyMedia(Long companyId) {
        log.info("Getting all media for company ID: {}", companyId);
        return companyMediaRepository.findByCompanyIdOrderByDisplayOrderAsc(companyId);
    }

    @Override
    public List<CompanyMedia> getCompanyMediaByType(Long companyId, MediaType type) {
        log.info("Getting media for company ID: {}, type: {}", companyId, type);
        return companyMediaRepository.findByCompanyIdAndTypeOrderByDisplayOrderAsc(companyId, type);
    }

    @Override
    @Transactional
    public void deleteMedia(Long mediaId) {
        log.info("Deleting media with ID: {}", mediaId);
        
        CompanyMedia media = companyMediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media not found with ID: " + mediaId));

        // Delete from Firebase Storage
        mediaStorageService.deleteFile(media.getUrl());

        // Delete from database
        companyMediaRepository.delete(media);
    }

    @Override
    @Transactional
    public void updateDisplayOrder(Long mediaId, Integer displayOrder) {
        log.info("Updating display order for media ID: {} to {}", mediaId, displayOrder);
        
        CompanyMedia media = companyMediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media not found with ID: " + mediaId));

        media.setDisplayOrder(displayOrder);
        companyMediaRepository.save(media);
    }
}

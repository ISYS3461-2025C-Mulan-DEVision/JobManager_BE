package com.devision.job_manager_company.service.impl;

import com.devision.job_manager_company.model.Company;
import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import com.devision.job_manager_company.repository.CompanyMediaRepository;
import com.devision.job_manager_company.repository.CompanyProfileRepository;
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
    private final CompanyProfileRepository companyProfileRepository;
    private final MediaStorageService mediaStorageService;

    @Override
    @Transactional
    public CompanyMedia uploadLogo(Long companyId, MultipartFile file) throws IOException {
        log.info("Uploading logo for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Delete old logo if exists
        List<CompanyMedia> existingLogos = companyMediaRepository
                .findByCompanyIdAndTypeOrderByDisplayOrderAsc(companyId, MediaType.LOGO);
        
        for (CompanyMedia oldLogo : existingLogos) {
            mediaStorageService.deleteFile(oldLogo.getUrl());
            companyMediaRepository.delete(oldLogo);
        }

        // Upload to Firebase Storage
        String url = mediaStorageService.uploadCompanyLogo(companyId, file);

        // Save new logo
        CompanyMedia logo = CompanyMedia.builder()
                .company(company)
                .type(MediaType.LOGO)
                .url(url)
                .title("Company Logo")
                .displayOrder(0)
                .build();

        CompanyMedia savedLogo = companyMediaRepository.save(logo);
        
        // Update logoUrl in CompanyProfile
        companyProfileRepository.findById(companyId).ifPresent(profile -> {
            profile.setLogoUrl(url);
            companyProfileRepository.save(profile);
            log.info("Updated logoUrl in CompanyProfile for company ID: {}", companyId);
        });
        
        return savedLogo;
    }

    @Override
    @Transactional
    public CompanyMedia uploadBanner(Long companyId, MultipartFile file) throws IOException {
        log.info("Uploading banner for company ID: {}", companyId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Delete old banner if exists
        List<CompanyMedia> existingBanners = companyMediaRepository
                .findByCompanyIdAndTypeOrderByDisplayOrderAsc(companyId, MediaType.BANNER);
        
        for (CompanyMedia oldBanner : existingBanners) {
            mediaStorageService.deleteFile(oldBanner.getUrl());
            companyMediaRepository.delete(oldBanner);
        }

        // Upload to Firebase Storage
        String url = mediaStorageService.uploadCompanyBanner(companyId, file);

        // Save new banner
        CompanyMedia banner = CompanyMedia.builder()
                .company(company)
                .type(MediaType.BANNER)
                .url(url)
                .title("Company Banner")
                .displayOrder(0)
                .build();

        CompanyMedia savedBanner = companyMediaRepository.save(banner);
        
        // Update bannerUrl in CompanyProfile
        companyProfileRepository.findById(companyId).ifPresent(profile -> {
            profile.setBannerUrl(url);
            companyProfileRepository.save(profile);
            log.info("Updated bannerUrl in CompanyProfile for company ID: {}", companyId);
        });
        
        return savedBanner;
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
        
        // Update CompanyProfile if deleting logo or banner
        if (media.getType() == MediaType.LOGO) {
            companyProfileRepository.findById(media.getCompany().getId()).ifPresent(profile -> {
                profile.setLogoUrl(null);
                companyProfileRepository.save(profile);
                log.info("Cleared logoUrl in CompanyProfile for company ID: {}", media.getCompany().getId());
            });
        } else if (media.getType() == MediaType.BANNER) {
            companyProfileRepository.findById(media.getCompany().getId()).ifPresent(profile -> {
                profile.setBannerUrl(null);
                companyProfileRepository.save(profile);
                log.info("Cleared bannerUrl in CompanyProfile for company ID: {}", media.getCompany().getId());
            });
        }
    }

    @Override
    @Transactional
    public void updateDisplayOrder(Long companyId, Long mediaId, Integer displayOrder) {
        log.info("Updating display order for media ID: {} to {} for company ID: {}", mediaId, displayOrder, companyId);
        
        CompanyMedia media = companyMediaRepository.findByIdAndCompanyId(mediaId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Media not found with ID: " + mediaId + " for company ID: " + companyId));

        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be >= 0");
        }

        media.setDisplayOrder(displayOrder);
        companyMediaRepository.save(media);
    }

    @Override
    @Transactional
    public void reorderMedia(Long companyId, List<Long> orderedMediaIds) {
        log.info("Reordering media for company ID: {}, new order: {}", companyId, orderedMediaIds);
        
        if (orderedMediaIds == null || orderedMediaIds.isEmpty()) {
            throw new IllegalArgumentException("orderedMediaIds must not be empty");
        }

        // Validate that all IDs belong to this company
        List<Long> existingIds = companyMediaRepository.findIdsByCompanyId(companyId);
        
        if (!existingIds.containsAll(orderedMediaIds)) {
            throw new IllegalArgumentException("Some media IDs do not belong to this company");
        }

        // Update display order for each media in the new order
        int order = 0;
        for (Long mediaId : orderedMediaIds) {
            companyMediaRepository.updateDisplayOrder(companyId, mediaId, order++);
        }
        
        log.info("Successfully reordered {} media items for company ID: {}", orderedMediaIds.size(), companyId);
    }
}

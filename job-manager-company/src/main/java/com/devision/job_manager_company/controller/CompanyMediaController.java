package com.devision.job_manager_company.controller;

import com.devision.job_manager_company.dto.ApiResponse;
import com.devision.job_manager_company.dto.CompanyMediaDto;
import com.devision.job_manager_company.dto.UpdateMediaDisplayOrderRequest;
import com.devision.job_manager_company.model.CompanyMedia;
import com.devision.job_manager_company.model.MediaType;
import com.devision.job_manager_company.service.CompanyMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies/{companyId}/media")
@RequiredArgsConstructor
@Slf4j
public class CompanyMediaController {

    private final CompanyMediaService companyMediaService;

    @PostMapping("/logo")
    public ResponseEntity<ApiResponse<CompanyMediaDto>> uploadLogo(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        log.info("Upload logo request for company ID: {}", companyId);
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is required"));
            }

            CompanyMedia media = companyMediaService.uploadLogo(companyId, file);
            CompanyMediaDto dto = mapToDto(media);
            
            return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully", dto));
        } catch (IllegalArgumentException e) {
            log.error("Failed to upload logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload logo", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload logo: " + e.getMessage()));
        }
    }

    @PostMapping("/banner")
    public ResponseEntity<ApiResponse<CompanyMediaDto>> uploadBanner(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        log.info("Upload banner request for company ID: {}", companyId);
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is required"));
            }

            CompanyMedia media = companyMediaService.uploadBanner(companyId, file);
            CompanyMediaDto dto = mapToDto(media);
            
            return ResponseEntity.ok(ApiResponse.success("Banner uploaded successfully", dto));
        } catch (IllegalArgumentException e) {
            log.error("Failed to upload banner: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload banner", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload banner: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyMediaDto>> uploadMedia(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType type,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        log.info("Upload media request for company ID: {}, type: {}", companyId, type);
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is required"));
            }

            CompanyMedia media = companyMediaService.uploadMedia(companyId, type, file, title, description);
            CompanyMediaDto dto = mapToDto(media);
            
            return ResponseEntity.ok(ApiResponse.success("Media uploaded successfully", dto));
        } catch (IllegalArgumentException e) {
            log.error("Failed to upload media: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload media", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload media: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyMediaDto>>> getCompanyMedia(
            @PathVariable Long companyId,
            @RequestParam(value = "type", required = false) MediaType type) {
        log.info("Get media request for company ID: {}, type: {}", companyId, type);
        
        try {
            List<CompanyMedia> mediaList = type != null
                    ? companyMediaService.getCompanyMediaByType(companyId, type)
                    : companyMediaService.getCompanyMedia(companyId);

            List<CompanyMediaDto> dtos = mediaList.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Media retrieved successfully", dtos));
        } catch (Exception e) {
            log.error("Failed to get company media", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get media: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<ApiResponse<String>> deleteMedia(
            @PathVariable Long companyId,
            @PathVariable Long mediaId) {
        log.info("Delete media request for company ID: {}, media ID: {}", companyId, mediaId);
        
        try {
            companyMediaService.deleteMedia(mediaId);
            return ResponseEntity.ok(ApiResponse.success("Media deleted successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete media: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete media", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to delete media: " + e.getMessage()));
        }
    }

    @PutMapping("/{mediaId}/display-order")
    public ResponseEntity<ApiResponse<String>> updateDisplayOrder(
            @PathVariable Long companyId,
            @PathVariable Long mediaId,
            @Valid @RequestBody UpdateMediaDisplayOrderRequest request) {
        log.info("Update display order request for company ID: {}, media ID: {}, new order: {}", 
                companyId, mediaId, request.getDisplayOrder());
        
        try {
            companyMediaService.updateDisplayOrder(companyId, mediaId, request.getDisplayOrder());
            return ResponseEntity.ok(ApiResponse.success("Display order updated successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update display order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update display order", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to update display order: " + e.getMessage()));
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse<String>> reorderMedia(
            @PathVariable Long companyId,
            @RequestBody List<Long> orderedMediaIds) {
        log.info("Reorder media request for company ID: {}, order: {}", companyId, orderedMediaIds);
        
        try {
            companyMediaService.reorderMedia(companyId, orderedMediaIds);
            return ResponseEntity.ok(ApiResponse.success("Media reordered successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to reorder media: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to reorder media", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to reorder media: " + e.getMessage()));
        }
    }

    private CompanyMediaDto mapToDto(CompanyMedia media) {
        return CompanyMediaDto.builder()
                .id(media.getId())
                .companyId(media.getCompany().getId())
                .type(media.getType())
                .url(media.getUrl())
                .title(media.getTitle())
                .description(media.getDescription())
                .displayOrder(media.getDisplayOrder())
                .createdAt(media.getCreatedAt())
                .build();
    }
}

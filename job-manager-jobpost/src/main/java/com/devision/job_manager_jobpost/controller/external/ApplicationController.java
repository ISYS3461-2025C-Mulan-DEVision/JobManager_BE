package com.devision.job_manager_jobpost.controller.external;

import com.devision.job_manager_jobpost.dto.ApiResponse;
import com.devision.job_manager_jobpost.dto.external.ApplicationResponseDto;
import com.devision.job_manager_jobpost.dto.external.PageableResponseDto;
import com.devision.job_manager_jobpost.service.external.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Get applications for a job post with pagination and filtering
     *
     * GET /api/applications/job-posts/{jobPostId}?companyId={companyId}&page=0&size=20&archived=false
     */
    @GetMapping("/job-posts/{jobPostId}")
    public ResponseEntity<ApiResponse<PageableResponseDto<ApplicationResponseDto>>> getApplicationsByJobPost(
            @PathVariable UUID jobPostId,
            @RequestParam UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean archived) {

        log.info("GET /api/applications/job-posts/{} - companyId: {}, page: {}, size: {}, archived: {}",
                jobPostId, companyId, page, size, archived);

        try {
            PageableResponseDto<ApplicationResponseDto> applications =
                    applicationService.getApplicationsByJobPost(jobPostId, companyId, page, size, archived);

            return ResponseEntity.ok(
                    ApiResponse.success("Applications retrieved successfully", applications)
            );
        } catch (Exception e) {
            log.error("Error fetching applications for job post {}: ", jobPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch applications: " + e.getMessage()));
        }
    }

    /**
     * Get application counts (pending and archived) for a job post
     *
     * GET /api/applications/job-posts/{jobPostId}/counts?companyId={companyId}
     */
    @GetMapping("/job-posts/{jobPostId}/counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getApplicationCounts(
            @PathVariable UUID jobPostId,
            @RequestParam UUID companyId) {

        log.info("GET /api/applications/job-posts/{}/counts - companyId: {}", jobPostId, companyId);

        try {
            long[] counts = applicationService.getApplicationCounts(jobPostId, companyId);

            Map<String, Long> countMap = new HashMap<>();
            countMap.put("pending", counts[0]);
            countMap.put("archived", counts[1]);

            return ResponseEntity.ok(
                    ApiResponse.success("Application counts retrieved successfully", countMap)
            );
        } catch (Exception e) {
            log.error("Error fetching application counts for job post {}: ", jobPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch application counts: " + e.getMessage()));
        }
    }

    /**
     * Archive an application
     *
     * POST /api/applications/{applicationId}/archive
     * Body: { "companyId": "uuid", "jobPostId": "uuid" }
     */
    @PostMapping("/{applicationId}/archive")
    public ResponseEntity<ApiResponse<Void>> archiveApplication(
            @PathVariable UUID applicationId,
            @RequestBody ArchiveRequest request) {

        log.info("POST /api/applications/{}/archive - companyId: {}, jobPostId: {}",
                applicationId, request.getCompanyId(), request.getJobPostId());

        try {
            applicationService.archiveApplication(applicationId, request.getCompanyId(), request.getJobPostId());

            return ResponseEntity.ok(
                    ApiResponse.success("Application archived successfully", null)
            );
        } catch (Exception e) {
            log.error("Error archiving application {}: ", applicationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to archive application: " + e.getMessage()));
        }
    }

    /**
     * Unarchive an application
     *
     * POST /api/applications/{applicationId}/unarchive
     * Body: { "companyId": "uuid" }
     */
    @PostMapping("/{applicationId}/unarchive")
    public ResponseEntity<ApiResponse<Void>> unarchiveApplication(
            @PathVariable UUID applicationId,
            @RequestBody UnarchiveRequest request) {

        log.info("POST /api/applications/{}/unarchive - companyId: {}",
                applicationId, request.getCompanyId());

        try {
            applicationService.unarchiveApplication(applicationId, request.getCompanyId());

            return ResponseEntity.ok(
                    ApiResponse.success("Application unarchived successfully", null)
            );
        } catch (Exception e) {
            log.error("Error unarchiving application {}: ", applicationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to unarchive application: " + e.getMessage()));
        }
    }

    /**
     * Download application file (Resume or Cover Letter)
     *
     * GET /api/applications/{applicationId}/files/{docType}
     * docType: RESUME or COVER_LETTER
     */
    @GetMapping("/{applicationId}/files/{docType}")
    public ResponseEntity<byte[]> downloadApplicationFile(
            @PathVariable UUID applicationId,
            @PathVariable String docType) {

        log.info("GET /api/applications/{}/files/{}", applicationId, docType);

        try {
            byte[] fileContent = applicationService.downloadApplicationFile(applicationId, docType);

            // Determine filename based on document type
            String filename = docType.equals("RESUME") ? "resume.pdf" : "cover-letter.pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", filename);
            headers.setContentLength(fileContent.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IllegalArgumentException e) {
            log.error("Invalid document type: {}", docType);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error downloading file for application {}: ", applicationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request DTOs
    @lombok.Data
    public static class ArchiveRequest {
        private UUID companyId;
        private UUID jobPostId;
    }

    @lombok.Data
    public static class UnarchiveRequest {
        private UUID companyId;
    }
}

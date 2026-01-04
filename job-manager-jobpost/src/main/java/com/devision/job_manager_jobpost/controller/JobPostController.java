package com.devision.job_manager_jobpost.controller;

import com.devision.job_manager_jobpost.dto.CreateJobPostRequest;
import com.devision.job_manager_jobpost.dto.JobPostDto;
import com.devision.job_manager_jobpost.dto.UpdateJobPostRequest;
import com.devision.job_manager_jobpost.dto.UpdateSkillsRequest;
import com.devision.job_manager_jobpost.dto.ApiResponse;
import com.devision.job_manager_jobpost.model.EmploymentType;
import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.model.JobPostEmploymentType;
import com.devision.job_manager_jobpost.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@Slf4j
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobPostDto>> createJobPost(
            @Valid @RequestBody CreateJobPostRequest request) {
        log.info("Creating job post for companyId={}", request.getCompanyId());

        JobPost jobPost = JobPost.builder()
                .companyId(request.getCompanyId())
                .title(request.getTitle())
                .description(request.getDescription())
                .salaryType(request.getSalaryType())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .salaryNote(request.getSalaryNote())
                .locationCity(request.getLocationCity())
                // .countryId(request.getCountryId())
                .fresher(request.isFresher())
                .aPrivate(request.isAPrivate())
                .expiryAt(request.getExpiryAt())
                .build();

        // Add employment type if provided
        if (request.getEmploymentType() != null) {
            JobPostEmploymentType empType = new JobPostEmploymentType();
            empType.setId(UUID.randomUUID());
            empType.setJobPost(jobPost);
            empType.setType(request.getEmploymentType());
            jobPost.getEmploymentTypes().add(empType);
        }

        JobPost created = jobPostService.createJobPost(jobPost);
        JobPostDto dto = mapToDto(created);
        return ResponseEntity.ok(ApiResponse.success("Job post created successfully", dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostDto>> getJobPost(@PathVariable UUID id) {
        log.info("Getting job post with ID: {}", id);

        return jobPostService.getJobPostById(id)
                .map(jobPost -> ResponseEntity.ok(ApiResponse.success("Job post found", mapToDto(jobPost))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<JobPostDto>>> getCompanyJobPosts(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostDto> dtoPage = jobPostService.getCompanyJobPosts(companyId, pageable)
                .map(this::mapToDto);
        return ResponseEntity.ok(ApiResponse.success("Company job posts fetched", dtoPage));
    }

    @GetMapping("/company/{companyId}/published")
    public ResponseEntity<ApiResponse<Page<JobPostDto>>> getPublishedCompanyJobPosts(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostDto> dtoPage = jobPostService.getPublishedCompanyJobPosts(companyId, pageable)
                .map(this::mapToDto);
        return ResponseEntity.ok(ApiResponse.success("Published company job posts fetched", dtoPage));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<JobPostDto>>> getPublishedJobPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostDto> dtoPage = jobPostService.getPublishedJobPosts(pageable)
                .map(this::mapToDto);
        return ResponseEntity.ok(ApiResponse.success("Published job posts fetched", dtoPage));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobPostDto>>> getAllJobPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all job posts with page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostDto> dtoPage = jobPostService.getAllJobPosts(pageable)
                .map(this::mapToDto);
        return ResponseEntity.ok(ApiResponse.success("All job posts fetched", dtoPage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobPostDto>> updateJobPost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateJobPostRequest request) {
        log.info("Updating job post with ID: {}", id);

        try {
            JobPost updated = JobPost.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .salaryType(request.getSalaryType())
                    .salaryMin(request.getSalaryMin())
                    .salaryMax(request.getSalaryMax())
                    .salaryNote(request.getSalaryNote())
                    .locationCity(request.getLocationCity())
                    // .countryId(request.getCountryId())
                    .fresher(request.getFresher() != null && request.getFresher())
                    .aPrivate(request.getAPrivate() != null && request.getAPrivate())
                    .expiryAt(request.getExpiryAt())
                    .build();

            JobPost jobPost = jobPostService.updateJobPost(id, updated);
            return ResponseEntity.ok(ApiResponse.success("Job post updated successfully", mapToDto(jobPost)));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update job post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<JobPostDto>> publishJobPost(@PathVariable UUID id) {
        log.info("Publishing job post with ID: {}", id);
        try {
            JobPost jobPost = jobPostService.publishJobPost(id);
            return ResponseEntity.ok(ApiResponse.success("Job post published", mapToDto(jobPost)));
        } catch (IllegalArgumentException e) {
            log.error("Failed to publish job post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<JobPostDto>> unpublishJobPost(@PathVariable UUID id) {
        log.info("Unpublishing job post with ID: {}", id);
        try {
            JobPost jobPost = jobPostService.unpublishJobPost(id);
            return ResponseEntity.ok(ApiResponse.success("Job post unpublished", mapToDto(jobPost)));
        } catch (IllegalArgumentException e) {
            log.error("Failed to unpublish job post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJobPost(@PathVariable UUID id) {
        log.info("Deleting job post with ID: {}", id);
        try {
            jobPostService.deleteJobPost(id);
            return ResponseEntity.ok(ApiResponse.success("Job post deleted successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete job post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update job post skills - CRITICAL for Ultimo 4.3.1
     * This endpoint triggers Kafka event for instant applicant notifications
     *
     * @param id The job post ID
     * @param request Contains the list of skill UUIDs
     * @return Updated job post with success message
     */
    @PutMapping("/{id}/skills")
    public ResponseEntity<ApiResponse<JobPostDto>> updateJobPostSkills(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSkillsRequest request) {
        log.info("Updating skills for job post ID: {} with {} skills", id, request.getSkillIds().size());

        try {
            JobPost updated = jobPostService.updateJobPostSkills(id, request.getSkillIds());
            return ResponseEntity.ok(ApiResponse.success(
                    "Job post skills updated successfully. Notifications sent to matching applicants.",
                    mapToDto(updated)
            ));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update job post skills: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private JobPostDto mapToDto(JobPost jobPost) {
        // Get first employment type if available (frontend expects single value)
        EmploymentType employmentType = null;
        if (jobPost.getEmploymentTypes() != null && !jobPost.getEmploymentTypes().isEmpty()) {
            employmentType = jobPost.getEmploymentTypes().get(0).getType();
        }

        // Fetch country code from Company service (cached) - Ultimo 4.3.1
        String countryCode = null;
        if (jobPost.getCompanyId() != null) {
            countryCode = ((com.devision.job_manager_jobpost.service.impl.JobPostServiceImpl) jobPostService)
                    .getCompanyCountry(jobPost.getCompanyId());
        }

        return JobPostDto.builder()
                .id(jobPost.getJobPostId())
                .companyId(jobPost.getCompanyId())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .fresher(jobPost.isFresher())
                .salaryType(jobPost.getSalaryType())
                .salaryMin(jobPost.getSalaryMin())
                .salaryMax(jobPost.getSalaryMax())
                .salaryNote(jobPost.getSalaryNote())
                .locationCity(jobPost.getLocationCity())
                .countryCode(countryCode)  // Derived from Company service
                .published(jobPost.isPublished())
                .aPrivate(jobPost.isAPrivate())
                .postedAt(jobPost.getPostedAt())
                .expiryAt(jobPost.getExpiryAt())
                .employmentType(employmentType)
                .build();
    }
}



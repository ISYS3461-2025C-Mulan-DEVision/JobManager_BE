package com.devision.job_manager_applicant_search.controller.internal;

import com.devision.job_manager_applicant_search.dto.ApiResponse;
import com.devision.job_manager_applicant_search.dto.internal.request.ApplicantSearchRequest;
import com.devision.job_manager_applicant_search.dto.internal.response.ApplicantResponse;
import com.devision.job_manager_applicant_search.service.ApplicantSearchService;
import com.devision.job_manager_applicant_search.service.ApplicantSearchService.ApplicantSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/applicants")
@RequiredArgsConstructor
public class ApplicantSearchController {

    private final ApplicantSearchService applicantSearchService;

    /**
     * Search for applicants using filter criteria.
     * 
     * Supported filters:
     * - keyword: Full-text search
     * - countryCode: Two-letter country code
     * - skills: Comma-separated skill names
     * - sortBy: Sort option (newest, etc.)
     * - page, pageSize: Pagination
     * 
     * TODO: Salary filtering - will be added when JA supports it
     * - minSalary, maxSalary
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ApplicantSearchResult>> searchApplicants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) List<String> employmentTypes,
            @RequestParam(required = false) String highestDegree,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
            // TODO: Salary filtering - uncomment when JA supports it
            // @RequestParam(required = false) BigDecimal minSalary,
            // @RequestParam(required = false) BigDecimal maxSalary
    ) {
        ApplicantSearchRequest request = ApplicantSearchRequest.builder()
                .keyword(keyword)
                .countryCode(countryCode)
                .skills(skills)
                .employmentTypes(employmentTypes)
                .highestDegree(highestDegree)
                .sortBy(sortBy)
                .page(page)
                .pageSize(size)
                // .minSalary(minSalary)
                // .maxSalary(maxSalary)
                .build();

        ApplicantSearchResult result = applicantSearchService.searchApplicants(request);
        return ResponseEntity.ok(ApiResponse.success("Applicants retrieved", result));
    }

    /**
     * Get all available skills for filter dropdown.
     * 
     * TODO: Skill endpoint may change based on JA team updates.
     */
    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<ApplicantResponse.SkillDto>>> getSkills() {
        List<ApplicantResponse.SkillDto> skills = applicantSearchService.getSkills();
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved", skills));
    }

    /**
     * Search skills by name for autocomplete.
     * 
     * @param q Search query
     */
    @GetMapping("/skills/search")
    public ResponseEntity<ApiResponse<List<ApplicantResponse.SkillDto>>> searchSkills(
            @RequestParam(required = false) String q) {
        List<ApplicantResponse.SkillDto> skills = applicantSearchService.searchSkills(q);
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved", skills));
    }
}

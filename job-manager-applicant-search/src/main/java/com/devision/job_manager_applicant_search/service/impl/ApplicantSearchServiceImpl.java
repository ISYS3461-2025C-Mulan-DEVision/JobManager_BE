package com.devision.job_manager_applicant_search.service.impl;

import com.devision.job_manager_applicant_search.client.ApplicantClient;
import com.devision.job_manager_applicant_search.dto.internal.ApplicantResponse;
import com.devision.job_manager_applicant_search.dto.internal.ApplicantSearchRequest;
import com.devision.job_manager_applicant_search.service.ApplicantSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ApplicantSearchService.
 * 
 * Delegates search to JA service and handles pagination/sorting locally.
 * 
 * Note: JA search endpoint currently supports: skills, country, keyword.
 * Other filters (employmentTypes, highestDegree) are not supported by JA.
 * 
 * TODO: Salary filtering - JA does not have salary fields yet.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantSearchServiceImpl implements ApplicantSearchService {

    private final ApplicantClient applicantClient;

    @Override
    public ApplicantSearchResult searchApplicants(ApplicantSearchRequest request) {
        log.info("Searching applicants with filters: keyword={}, country={}, skills={}",
                request.getKeyword(), request.getCountryCode(), request.getSkills());

        // Build skills parameter (comma-separated)
        String skillsParam = null;
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            skillsParam = String.join(",", request.getSkills());
        }

        // Call JA service
        List<ApplicantResponse> allResults = applicantClient.searchApplicants(
                skillsParam,
                request.getCountryCode(),
                request.getKeyword()
        );

        // Apply sorting (JA doesn't support sorting, so we do it here)
        allResults = applySorting(allResults, request.getSortBy());

        // Apply pagination
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getPageSize() != null ? request.getPageSize() : 10;
        
        int totalElements = allResults.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        
        List<ApplicantResponse> pagedResults = fromIndex < totalElements
                ? allResults.subList(fromIndex, toIndex)
                : List.of();

        log.info("Returning {} of {} total applicants (page {} of {})",
                pagedResults.size(), totalElements, page, totalPages);

        return new ApplicantSearchResult(
                pagedResults,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }

    @Override
    public List<ApplicantResponse.SkillDto> getSkills() {
        return applicantClient.getAllSkills();
    }

    @Override
    public List<ApplicantResponse.SkillDto> searchSkills(String query) {
        return applicantClient.searchSkills(query);
    }

    /**
     * Apply sorting to applicant list.
     * 
     * @param applicants List to sort
     * @param sortBy Sort option
     * @return Sorted list
     */
    private List<ApplicantResponse> applySorting(List<ApplicantResponse> applicants, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "newest";
        }

        Comparator<ApplicantResponse> comparator;

        switch (sortBy) {
            case "newest":
                comparator = Comparator.comparing(
                        ApplicantResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );
                break;
            // TODO: Salary sorting - uncomment when JA adds salary fields
            // case "salaryAsc":
            //     comparator = Comparator.comparing(ApplicantResponse::getDesiredSalary,
            //             Comparator.nullsLast(Comparator.naturalOrder()));
            //     break;
            // case "salaryDesc":
            //     comparator = Comparator.comparing(ApplicantResponse::getDesiredSalary,
            //             Comparator.nullsLast(Comparator.reverseOrder()));
            //     break;
            default:
                // Default to newest
                comparator = Comparator.comparing(
                        ApplicantResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );
        }

        return applicants.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}

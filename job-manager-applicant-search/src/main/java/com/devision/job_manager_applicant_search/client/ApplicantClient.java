package com.devision.job_manager_applicant_search.client;

import com.devision.job_manager_applicant_search.dto.ApiResponse;
import com.devision.job_manager_applicant_search.dto.internal.response.ApplicantResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Client for calling Job Applicant service's user search endpoints.
 * 
 * TODO: Applicant Data Dependency
 * The exact API contract is owned by the Job Applicant team.
 * Endpoint paths and response structure may change.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    
    private final WebClient applicantWebClient;

    /**
     * Search for applicants using JA service's search endpoint.
     * 
     * @param skills Comma-separated list of skill names (OR matching)
     * @param country Two-letter country code
     * @param keyword Full-text search keyword
     * @return List of matching applicants
     */
    public List<ApplicantResponse> searchApplicants(String skills, String country, String keyword) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromPath("/api/v1/users/search");

            if (skills != null && !skills.isEmpty()) {
                uriBuilder.queryParam("skills", skills);
            }
            if (country != null && !country.isEmpty()) {
                uriBuilder.queryParam("country", country);
            }
            if (keyword != null && !keyword.isEmpty()) {
                uriBuilder.queryParam("keyword", keyword);
            }

            String uri = uriBuilder.build().toUriString();
            log.debug("Calling JA search endpoint: {}", uri);

            ApiResponse<List<ApplicantResponse>> response = applicantWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ApplicantResponse>>>() {})
                    .timeout(TIMEOUT)
                    .block();

            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("Received {} applicants from JA service", response.getData().size());
                return response.getData();
            }

            log.warn("JA search returned empty or unsuccessful response");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to search applicants from JA service: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get all active users from JA service.
     * 
     * @return List of all active applicants
     */
    public List<ApplicantResponse> getAllApplicants() {
        try {
            ApiResponse<List<ApplicantResponse>> response = applicantWebClient.get()
                    .uri("/api/v1/users")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ApplicantResponse>>>() {})
                    .timeout(TIMEOUT)
                    .block();

            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("Received {} applicants from JA service", response.getData().size());
                return response.getData();
            }

            log.warn("JA get all users returned empty or unsuccessful response");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to get all applicants from JA service: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get all skills from JA service.
     * 
     * TODO: Skill endpoint may change
     * Using /api/v1/skills endpoint from JA SkillController.
     * 
     * @return List of all available skills
     */
    public List<ApplicantResponse.SkillDto> getAllSkills() {
        try {
            ApiResponse<List<ApplicantResponse.SkillDto>> response = applicantWebClient.get()
                    .uri("/api/v1/skills")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ApplicantResponse.SkillDto>>>() {})
                    .timeout(TIMEOUT)
                    .block();

            if (response != null && response.isSuccess() && response.getData() != null) {
                log.debug("Received {} skills from JA service", response.getData().size());
                return response.getData();
            }

            log.warn("JA get all skills returned empty or unsuccessful response");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to get skills from JA service: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Search skills by name from JA service.
     * 
     * @param query Search query
     * @return List of matching skills
     */
    public List<ApplicantResponse.SkillDto> searchSkills(String query) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromPath("/api/v1/skills/search");
            
            if (query != null && !query.isEmpty()) {
                uriBuilder.queryParam("q", query);
            }

            ApiResponse<List<ApplicantResponse.SkillDto>> response = applicantWebClient.get()
                    .uri(uriBuilder.build().toUriString())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ApplicantResponse.SkillDto>>>() {})
                    .timeout(TIMEOUT)
                    .block();

            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to search skills from JA service: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

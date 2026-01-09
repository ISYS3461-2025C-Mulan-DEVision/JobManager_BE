package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.client.CompanyServiceClient;
import com.devision.job_manager_jobpost.service.CompanyIntegrationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of CompanyIntegrationService.
 * Handles integration with Company service via Feign client.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyIntegrationServiceImpl implements CompanyIntegrationService {

    private final CompanyServiceClient companyServiceClient;

    /**
     * Get company country code with caching (Ultimo 4.3.1 requirement).
     * Cached for 1 hour to minimize calls to Company service.
     *
     * @param companyId The company UUID
     * @return Country code (e.g., "VN", "AUS", "USA") or null if not found/unavailable
     */
    @Override
    @Cacheable(value = "companyCountry", key = "#companyId")
    public String getCompanyCountry(UUID companyId) {
        try {
            log.debug("Fetching country code for company ID: {} from Company service", companyId);
            String countryCode = companyServiceClient.getCompanyCountry(companyId);
            log.debug("Retrieved country code: {} for company ID: {}", countryCode, companyId);
            return countryCode;
        } catch (FeignException.NotFound e) {
            log.warn("Company not found: {}", companyId);
            return null;
        } catch (Exception e) {
            log.error("Failed to fetch country for company {}: {}", companyId, e.getMessage());
            return null; // Graceful degradation
        }
    }

    @Override
    public boolean companyExists(UUID companyId) {
        try {
            log.debug("Checking if company exists: {}", companyId);
            companyServiceClient.getCompanyCountry(companyId);
            return true;
        } catch (FeignException.NotFound e) {
            log.debug("Company not found: {}", companyId);
            return false;
        } catch (Exception e) {
            log.error("Error checking if company exists {}: {}", companyId, e.getMessage());
            // Assume company exists if we can't verify (fail-open for availability)
            return true;
        }
    }

    @Override
    @CacheEvict(value = "companyCountry", key = "#companyId")
    public void clearCompanyCache(UUID companyId) {
        log.info("Cleared company cache for company ID: {}", companyId);
    }

    @Override
    @CacheEvict(value = "companyCountry", allEntries = true)
    public void clearAllCompanyCaches() {
        log.info("Cleared all company caches");
    }
}

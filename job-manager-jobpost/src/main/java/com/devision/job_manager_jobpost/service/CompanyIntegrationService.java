package com.devision.job_manager_jobpost.service;

import java.util.UUID;

/**
 * Service interface for integration with Company service.
 * Handles all operations related to company data retrieval.
 */
public interface CompanyIntegrationService {

    /**
     * Get company country code with caching (Ultimo 4.3.1 requirement).
     * Cached for 1 hour to minimize calls to Company service.
     *
     * @param companyId The company UUID
     * @return Country code (e.g., "VN", "AUS", "USA") or null if not found/unavailable
     */
    String getCompanyCountry(UUID companyId);

    /**
     * Check if company exists
     */
    boolean companyExists(UUID companyId);

    /**
     * Clear company cache for a specific company
     */
    void clearCompanyCache(UUID companyId);

    /**
     * Clear all company caches
     */
    void clearAllCompanyCaches();
}

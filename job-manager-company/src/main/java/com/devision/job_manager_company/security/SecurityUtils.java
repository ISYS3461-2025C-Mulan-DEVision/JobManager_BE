package com.devision.job_manager_company.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Get the currently authenticated company principal.
     *
     * @return the CompanyPrincipal of the authenticated user
     * @throws AccessDeniedException if no user is authenticated
     */
    public CompanyPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CompanyPrincipal) {
            return (CompanyPrincipal) principal;
        }
        
        throw new AccessDeniedException("Invalid authentication principal");
    }

    /**
     * Get the company ID of the currently authenticated user.
     *
     * @return the company UUID
     * @throws AccessDeniedException if no user is authenticated
     */
    public UUID getCurrentCompanyId() {
        return getCurrentPrincipal().getCompanyId();
    }

    /**
     * Verify that the current user is authorized to access a specific company's resources.
     * Throws AccessDeniedException if the user is trying to access another company's data.
     *
     * @param companyId the company ID being accessed
     * @throws AccessDeniedException if the user is not authorized
     */
    public void verifyCompanyAccess(UUID companyId) {
        UUID currentCompanyId = getCurrentCompanyId();
        
        if (!currentCompanyId.equals(companyId)) {
            throw new AccessDeniedException(
                    "Access denied: You can only access your own company data"
            );
        }
    }

    /**
     * Check if the current user has access to a specific company's resources.
     *
     * @param companyId the company ID to check
     * @return true if the user is authorized, false otherwise
     */
    public boolean hasCompanyAccess(UUID companyId) {
        try {
            UUID currentCompanyId = getCurrentCompanyId();
            return currentCompanyId.equals(companyId);
        } catch (AccessDeniedException e) {
            return false;
        }
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param role the role to check (without "ROLE_" prefix)
     * @return true if the user has the role
     */
    public boolean hasRole(String role) {
        return getCurrentPrincipal().hasRole(role);
    }
}

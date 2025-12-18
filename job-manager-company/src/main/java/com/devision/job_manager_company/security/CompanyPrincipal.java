package com.devision.job_manager_company.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CompanyPrincipal implements Principal {

    private final UUID companyId;
    private final String email;
    private final String role;

    @Override
    public String getName() {
        return companyId.toString();
    }

    /**
     * Check if this principal has a specific role.
     */
    public boolean hasRole(String roleName) {
        return role != null && role.equalsIgnoreCase(roleName);
    }
}

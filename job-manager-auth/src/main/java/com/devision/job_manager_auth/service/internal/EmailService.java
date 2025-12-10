package com.devision.job_manager_auth.service.internal;

import com.devision.job_manager_auth.entity.Company;

// Handle email notification to the company
public interface EmailService {
    void sendActivationEmail(Company company, String activationToken);

    void sendWelcomeEmail(Company company);

    void sendAccountLockedEmail(Company company);
}

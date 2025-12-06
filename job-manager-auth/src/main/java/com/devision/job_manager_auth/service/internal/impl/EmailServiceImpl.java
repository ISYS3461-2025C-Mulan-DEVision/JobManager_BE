package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.service.internal.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendActivationEmail(Company company, String activationToken) {

        try {

            String activationLink = frontendUrl + "/activate?token=" + activationToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(company.getEmail());
            message.setSubject("Activate Your DEVision-JM Account");
            message.setText(String.format("""
                    Hello %s,
                    
                    Welcome to DEVision-JM! Please click the link below to activate your account: %s
                    """,
                    company.getName() != null ? company.getName() : "there",
                    activationLink)
            );

            mailSender.send(message);
            log.info("Activation email sent to {}", company.getEmail());

        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", company.getEmail(), e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(Company company) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(company.getEmail());
            message.setSubject("Welcome to DEVision Job Manager!");
            message.setText(String.format("""
                Hello %s,
                
                Your account has been successfully activated!
                
                You can now:
                - Post job opportunities
                - Search for qualified applicants
                - Manage your company profile
                
                Login here: %s/login
                
                Best regards,
                DEVision Team
                """,
                    company.getName() != null ? company.getName() : "there",
                    frontendUrl
            ));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", company.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", company.getEmail(), e.getMessage());
        }
    }

    @Override
    public void sendAccountLockedEmail(Company company) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(company.getEmail());
            message.setSubject("DEVision Account Security Alert - Account Locked");
            message.setText(String.format("""
                Hello %s,
                
                Your account has been temporarily locked due to multiple failed login attempts.
                
                If this was you, please wait 60 seconds before trying again.
                
                If this wasn't you, please contact our support team immediately.
                
                Best regards,
                DEVision Security Team
                """,
                    company.getName() != null ? company.getName() : "there"
            ));

            mailSender.send(message);
            log.info("Account locked email sent to: {}", company.getEmail());

        } catch (Exception e) {
            log.error("Failed to send account locked email to {}: {}", company.getEmail(), e.getMessage());
        }
    }
}

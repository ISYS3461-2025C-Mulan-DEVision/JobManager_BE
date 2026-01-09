package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.event.JobPostSkillsChangedEvent;
import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.model.JobPostSkill;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.repository.JobPostSkillRepository;
import com.devision.job_manager_jobpost.service.CompanyIntegrationService;
import com.devision.job_manager_jobpost.service.JobPostSkillsService;
import com.devision.job_manager_jobpost.service.internal.EventPublisherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of JobPostSkillsService.
 * Handles all operations related to job post skills.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostSkillsServiceImpl implements JobPostSkillsService {

    private final JobPostRepository jobPostRepository;
    private final JobPostSkillRepository jobPostSkillRepository;
    private final EventPublisherService eventPublisher;
    private final CompanyIntegrationService companyIntegrationService;

    /**
     * Update job post skills and publish Kafka event for Ultimo 4.3.1
     * CRITICAL: This enables instant notifications to matching applicants
     */
    @Override
    @Transactional
    public JobPost updateJobPostSkills(UUID jobPostId, List<UUID> newSkillIds) {
        log.info("Updating skills for job post ID: {}", jobPostId);

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + jobPostId));

        // Get current skills before update
        List<UUID> currentSkillIds = jobPost.getSkills().stream()
                .map(JobPostSkill::getSkillId)
                .toList();

        // Calculate what changed
        List<UUID> addedSkills = newSkillIds.stream()
                .filter(skillId -> !currentSkillIds.contains(skillId))
                .toList();

        List<UUID> removedSkills = currentSkillIds.stream()
                .filter(skillId -> !newSkillIds.contains(skillId))
                .toList();

        // Update the database
        jobPost.getSkills().clear();
        newSkillIds.forEach(skillId -> {
            JobPostSkill jobPostSkill = new JobPostSkill();
            jobPostSkill.setId(UUID.randomUUID());
            jobPostSkill.setJobPost(jobPost);
            jobPostSkill.setSkillId(skillId);
            jobPost.getSkills().add(jobPostSkill);
        });

        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // CRITICAL: Publish Kafka event AFTER database commit for Ultimo 4.3.1
        if (!addedSkills.isEmpty() || !removedSkills.isEmpty()) {
            log.info("Publishing skills changed event. Added: {}, Removed: {}",
                    addedSkills.size(), removedSkills.size());

            // Fetch country code from Company service (cached)
            String countryCode = companyIntegrationService.getCompanyCountry(savedJobPost.getCompanyId());

            JobPostSkillsChangedEvent event = JobPostSkillsChangedEvent.builder()
                    .jobPostId(savedJobPost.getJobPostId())
                    .companyId(savedJobPost.getCompanyId())
                    .title(savedJobPost.getTitle())
                    .locationCity(savedJobPost.getLocationCity())
                    .countryCode(countryCode)
                    .addedSkills(addedSkills)
                    .removedSkills(removedSkills)
                    .currentSkills(newSkillIds)
                    .changedAt(LocalDateTime.now())
                    .build();

            eventPublisher.publishJobPostSkillsChanged(event);
        } else {
            log.info("No skills changed for job post ID: {}", jobPostId);
        }

        return savedJobPost;
    }

    @Override
    @Transactional
    public JobPost addSkillToJobPost(UUID jobPostId, UUID skillId) {
        log.info("Adding skill {} to job post {}", skillId, jobPostId);

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + jobPostId));

        // Check if skill already exists
        boolean skillExists = jobPost.getSkills().stream()
                .anyMatch(jps -> jps.getSkillId().equals(skillId));

        if (skillExists) {
            log.warn("Skill {} already exists for job post {}", skillId, jobPostId);
            return jobPost;
        }

        JobPostSkill jobPostSkill = new JobPostSkill();
        jobPostSkill.setId(UUID.randomUUID());
        jobPostSkill.setJobPost(jobPost);
        jobPostSkill.setSkillId(skillId);
        jobPost.getSkills().add(jobPostSkill);

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully added skill to job post");

        return savedJobPost;
    }

    @Override
    @Transactional
    public JobPost removeSkillFromJobPost(UUID jobPostId, UUID skillId) {
        log.info("Removing skill {} from job post {}", skillId, jobPostId);

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + jobPostId));

        jobPost.getSkills().removeIf(jps -> jps.getSkillId().equals(skillId));

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        log.info("Successfully removed skill from job post");

        return savedJobPost;
    }

    @Override
    public List<JobPostSkill> getJobPostSkills(UUID jobPostId) {
        log.debug("Fetching skills for job post: {}", jobPostId);
        return jobPostSkillRepository.findByJobPostId(jobPostId);
    }

    @Override
    public boolean jobPostHasSkill(UUID jobPostId, UUID skillId) {
        log.debug("Checking if job post {} has skill {}", jobPostId, skillId);
        return jobPostSkillRepository.existsByJobPostIdAndSkillId(jobPostId, skillId);
    }

    @Override
    public long countSkillsForJobPost(UUID jobPostId) {
        log.debug("Counting skills for job post: {}", jobPostId);
        return jobPostSkillRepository.countByJobPostId(jobPostId);
    }

    @Override
    @Transactional
    public void clearJobPostSkills(UUID jobPostId) {
        log.info("Clearing all skills for job post: {}", jobPostId);
        jobPostSkillRepository.deleteByJobPostId(jobPostId);
        log.info("Successfully cleared all skills for job post: {}", jobPostId);
    }
}

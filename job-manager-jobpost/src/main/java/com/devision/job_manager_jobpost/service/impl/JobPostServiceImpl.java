package com.devision.job_manager_jobpost.service.impl;

import com.devision.job_manager_jobpost.client.CompanyServiceClient;
import com.devision.job_manager_jobpost.event.JobPostSkillsChangedEvent;
import com.devision.job_manager_jobpost.model.JobPost;
import com.devision.job_manager_jobpost.model.JobPostSkill;
import com.devision.job_manager_jobpost.repository.JobPostRepository;
import com.devision.job_manager_jobpost.service.JobPostService;
import com.devision.job_manager_jobpost.service.internal.EventPublisherService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;
    private final EventPublisherService eventPublisher;
    private final CompanyServiceClient companyServiceClient;

    @Override
    @Transactional
    public JobPost createJobPost(JobPost jobPost) {
        log.info("Creating job post for companyId={}", jobPost.getCompanyId());
        if (!jobPost.isPublished()) {
            jobPost.setPostedAt(null);
        }
        return jobPostRepository.save(jobPost);
    }

    @Override
    public Optional<JobPost> getJobPostById(UUID id) {
        return jobPostRepository.findById(id);
    }

    @Override
    public Page<JobPost> getCompanyJobPosts(UUID companyId, Pageable pageable) {
        return jobPostRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedCompanyJobPosts(UUID companyId, Pageable pageable) {
        return jobPostRepository.findByPublishedTrueAndCompanyId(companyId, pageable);
    }

    @Override
    public Page<JobPost> getPublishedJobPosts(Pageable pageable) {
        return jobPostRepository.findByPublishedTrueAndExpiryAtAfter(LocalDateTime.now(), pageable);
    }

    @Override
    @Transactional
    public JobPost updateJobPost(UUID id, JobPost updatedJobPost) {
        JobPost existing = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        if (updatedJobPost.getTitle() != null) {
            existing.setTitle(updatedJobPost.getTitle());
        }
        if (updatedJobPost.getDescription() != null) {
            existing.setDescription(updatedJobPost.getDescription());
        }
        if (updatedJobPost.getSalaryType() != null) {
            existing.setSalaryType(updatedJobPost.getSalaryType());
        }
        if (updatedJobPost.getSalaryMin() != null) {
            existing.setSalaryMin(updatedJobPost.getSalaryMin());
        }
        if (updatedJobPost.getSalaryMax() != null) {
            existing.setSalaryMax(updatedJobPost.getSalaryMax());
        }
        if (updatedJobPost.getSalaryNote() != null) {
            existing.setSalaryNote(updatedJobPost.getSalaryNote());
        }
        if (updatedJobPost.getLocationCity() != null) {
            existing.setLocationCity(updatedJobPost.getLocationCity());
        }
        // if (updatedJobPost.getCountryId() != null) {
        //     existing.setCountryId(updatedJobPost.getCountryId());
        // }
        // Flags and dates can be updated explicitly
        existing.setFresher(updatedJobPost.isFresher());
        if (updatedJobPost.getExpiryAt() != null) {
            existing.setExpiryAt(updatedJobPost.getExpiryAt());
        }

        return jobPostRepository.save(existing);
    }

    @Override
    @Transactional
    public JobPost publishJobPost(UUID id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(true);
        jobPost.setPostedAt(LocalDateTime.now());

        return jobPostRepository.save(jobPost);
    }

    @Override
    @Transactional
    public JobPost unpublishJobPost(UUID id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job post not found with ID: " + id));

        jobPost.setPublished(false);

        return jobPostRepository.save(jobPost);
    }

    @Override
    @Transactional
    public void deleteJobPost(UUID id) {
        if (!jobPostRepository.existsById(id)) {
            throw new IllegalArgumentException("Job post not found with ID: " + id);
        }
        jobPostRepository.deleteById(id);
    }

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
            String countryCode = getCompanyCountry(savedJobPost.getCompanyId());

            JobPostSkillsChangedEvent event = JobPostSkillsChangedEvent.builder()
                    .jobPostId(savedJobPost.getJobPostId())
                    .companyId(savedJobPost.getCompanyId())
                    .title(savedJobPost.getTitle())
                    .locationCity(savedJobPost.getLocationCity())
                    .countryCode(countryCode)  // Derived from Company service (Ultimo 4.3.1)
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

    /**
     * Get company country code with caching (Ultimo 4.3.1 requirement).
     * Cached for 1 hour to minimize calls to Company service.
     *
     * @param companyId The company UUID
     * @return Country code (e.g., "VN", "AUS", "USA") or null if not found/unavailable
     */
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
}



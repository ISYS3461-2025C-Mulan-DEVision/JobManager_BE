package com.devision.job_manager_jobpost.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event published when skills are added or removed from a job post.
 * This is CRITICAL for Ultimo requirement 4.3.1:
 * "Profile updates must be propagated to a Kafka topic when the company
 * changes skills of a job post. This enables all subscribed Job Applicants
 * to be notified instantly if the applicant's technical background matches
 * the job post criteria."
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostSkillsChangedEvent {
    private UUID jobPostId;
    private UUID companyId;
    private String title;
    private String locationCity;
    private String countryCode;

    /**
     * Skills that were added in this update
     */
    private List<UUID> addedSkills;

    /**
     * Skills that were removed in this update
     */
    private List<UUID> removedSkills;

    /**
     * All current skills after the update
     * (Used by applicant service for matching)
     */
    private List<UUID> currentSkills;

    private LocalDateTime changedAt;
}

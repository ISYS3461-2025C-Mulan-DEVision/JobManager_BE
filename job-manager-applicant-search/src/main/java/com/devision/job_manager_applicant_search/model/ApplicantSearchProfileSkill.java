package com.devision.job_manager_applicant_search.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "applicant_search_profile_skill")
@IdClass(ApplicantSearchProfileSkill.ApplicantSearchProfileSkillId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSearchProfileSkill {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ApplicantSearchProfile profile;

    @Id
    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicantSearchProfileSkillId implements Serializable {
        private UUID profile;
        private UUID skillId;
    }
}

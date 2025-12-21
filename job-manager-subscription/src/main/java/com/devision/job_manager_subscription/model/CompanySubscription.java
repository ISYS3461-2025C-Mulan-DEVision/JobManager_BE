package com.devision.job_manager_subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_subscription", indexes = {
    @Index(name = "idx_company_subscription_company_id", columnList = "company_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "start_at", nullable = false)
    @Builder.Default
    private OffsetDateTime startAt = OffsetDateTime.now();

    @Column(name = "end_at")
    private OffsetDateTime endAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Checks if the subscription is currently premium (active and not expired)
    public boolean isPremium() {
        if (status != SubscriptionStatus.ACTIVE) {
            return false;
        }
        if (endAt == null) {
            return true;
        }
        return endAt.isAfter(OffsetDateTime.now());
    }
}

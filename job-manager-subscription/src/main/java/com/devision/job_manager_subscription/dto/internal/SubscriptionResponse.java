package com.devision.job_manager_subscription.dto.internal;

import com.devision.job_manager_subscription.model.CompanySubscription;
import com.devision.job_manager_subscription.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Full subscription response DTO for internal APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private UUID id;
    private UUID companyId;
    private SubscriptionStatus status;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private boolean isPremium;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static SubscriptionResponse fromEntity(CompanySubscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .companyId(subscription.getCompanyId())
                .status(subscription.getStatus())
                .startAt(subscription.getStartAt())
                .endAt(subscription.getEndAt())
                .isPremium(subscription.isPremium())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}

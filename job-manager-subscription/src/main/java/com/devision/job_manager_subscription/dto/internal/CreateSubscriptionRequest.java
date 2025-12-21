package com.devision.job_manager_subscription.dto.internal;

import com.devision.job_manager_subscription.model.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Request DTO for creating a new subscription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    private SubscriptionStatus status;

    private OffsetDateTime startAt;

    private OffsetDateTime endAt;
}

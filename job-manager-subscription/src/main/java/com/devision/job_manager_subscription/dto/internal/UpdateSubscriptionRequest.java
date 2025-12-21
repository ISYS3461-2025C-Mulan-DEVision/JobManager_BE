package com.devision.job_manager_subscription.dto.internal;

import com.devision.job_manager_subscription.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Request DTO for updating an existing subscription.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubscriptionRequest {

    private SubscriptionStatus status;

    private OffsetDateTime endAt;
}

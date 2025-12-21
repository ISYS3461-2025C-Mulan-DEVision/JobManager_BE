package com.devision.job_manager_applicant_search.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUpdatedEvent {

    private UUID companyId;
    
    private String status;
    
    private OffsetDateTime endAt;
    
    private boolean isPremium;
    
    private OffsetDateTime timestamp;
}

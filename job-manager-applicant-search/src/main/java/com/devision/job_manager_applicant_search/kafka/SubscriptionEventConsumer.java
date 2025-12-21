package com.devision.job_manager_applicant_search.kafka;

import com.devision.job_manager_applicant_search.client.SubscriptionClient;
import com.devision.job_manager_applicant_search.event.SubscriptionUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for subscription update events.
 * Updates the local Redis cache with subscription/premium status
 * when the Subscription Service publishes changes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventConsumer {

    private final SubscriptionClient subscriptionClient;

    /**
     * Consumes subscription updated events and updates local cache.
     * 
     * @param event the subscription updated event
     */
    @KafkaListener(
            topics = "company.subscription.updated",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onSubscriptionUpdated(SubscriptionUpdatedEvent event) {
        log.info("Received subscription updated event for company: {}", event.getCompanyId());
        
        try {
            subscriptionClient.updatePremiumStatus(event);
            log.info("Updated subscription cache for company {}: isPremium={}", 
                    event.getCompanyId(), event.isPremium());
        } catch (Exception e) {
            log.error("Error updating subscription cache for company {}: {}", 
                    event.getCompanyId(), e.getMessage(), e);
            // In production, consider retry mechanism
        }
    }
}

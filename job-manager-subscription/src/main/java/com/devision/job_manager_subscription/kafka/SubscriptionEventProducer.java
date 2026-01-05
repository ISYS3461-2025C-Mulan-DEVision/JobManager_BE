package com.devision.job_manager_subscription.kafka;

import com.devision.job_manager_subscription.event.SubscriptionUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventProducer {

    private static final String TOPIC_SUBSCRIPTION_UPDATED = "company.subscription.updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a subscription updated event.
     * The company ID is used as the key for partition ordering.
     */
    public void publishSubscriptionUpdated(SubscriptionUpdatedEvent event) {
        log.info("Publishing subscription updated event for company: {}", event.getCompanyId());
        kafkaTemplate.send(TOPIC_SUBSCRIPTION_UPDATED, event.getCompanyId().toString(), event);
    }
}

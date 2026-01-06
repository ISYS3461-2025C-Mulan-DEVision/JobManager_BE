package com.devision.job_manager_subscription.kafka;


import com.devision.job_manager_subscription.dto.internal.event.PaymentCompletedEvent;
import com.devision.job_manager_subscription.dto.internal.request.CreateSubscriptionRequest;
import com.devision.job_manager_subscription.model.SubscriptionStatus;
import com.devision.job_manager_subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final SubscriptionService subscriptionService;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "subscription-service-group",
            containerFactory = "paymentCompletedKafkaListenerFactory"
    )
    public void consumePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment completed event for payer: {}", event.getPayerId());

        try {
            // Check if subscription already exists
            var existingSubscription = subscriptionService.getByCompanyId(event.getPayerId());

            if (existingSubscription != null) {
                // Renew existing subscription
                log.info("Renewing subscription for payer: {}", event.getPayerId());

                // Calculate the new end date
                LocalDateTime endDate = LocalDateTime.now().plusDays(30);

                subscriptionService.update(
                        existingSubscription.getId(),
                        com.devision.job_manager_subscription.dto.internal.request.UpdateSubscriptionRequest.builder()
                                .status(SubscriptionStatus.ACTIVE)
                                .endAt(endDate)
                                .build()
                );

                log.info("Subscription renewed for payer: {}", event.getPayerId());


            } else {
                // Create new subscription
                log.info("Creating a new subscription for payer: {}", event.getPayerId());

                CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                        .companyId(event.getPayerId())
                        .status(SubscriptionStatus.ACTIVE)
                        .startAt(LocalDateTime.now())
                        .endAt(LocalDateTime.now().plusDays(30))
                        .build();

                log.info("New subscription created for company: {}", event.getPayerId());
                subscriptionService.create(request);
            }
        } catch (Exception e) {
            log.error("Error processing payment completed event for payer: {}", event.getPayerId(), e);
        }
    }

    @KafkaListener(
            topics = "payment.failed",
            groupId = "subscription-service-group",
            containerFactory = "genericKafkaListenerFactory"
    )
    public void consumePaymentFailed(Object event) {
        log.warn("Payment failed event received: {}", event);
    }

    @KafkaListener(
            topics = "payment.cancelled",
            groupId = "subscription-service-group",
            containerFactory = "genericKafkaListenerFactory"
    )
    public void consumePaymentCancelled(Object event) {
        log.info("Payment cancelled event received: {}", event);
    }
}

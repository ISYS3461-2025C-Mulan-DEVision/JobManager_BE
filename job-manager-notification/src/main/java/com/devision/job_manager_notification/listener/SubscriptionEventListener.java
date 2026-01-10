package com.devision.job_manager_notification.listener;

import com.devision.job_manager_notification.dto.internal.InternalCreateNotificationRequest;
import com.devision.job_manager_notification.enums.NotificationType;
import com.devision.job_manager_notification.event.SubscriptionCancelledEvent;
import com.devision.job_manager_notification.event.SubscriptionCreatedEvent;
import com.devision.job_manager_notification.event.SubscriptionExpiredEvent;
import com.devision.job_manager_notification.event.SubscriptionExpiringSoonEvent;
import com.devision.job_manager_notification.event.SubscriptionRenewedEvent;
import com.devision.job_manager_notification.service.InternalNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventListener {

    private final InternalNotificationService internalNotificationService;

    // Date formatter for displaying dates in notifications
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    /**
     * Handles SubscriptionCreatedEvent to send a welcome notification.
     *
     * This method is triggered when a company successfully signs up for a premium subscription.
     * It creates a welcome notification congratulating them and outlining the premium features.
     *
     * @param event the subscription created event
     * @param partition the Kafka partition this message came from
     * @param offset the offset of this message in the partition
     */
    @KafkaListener(
            topics = "subscription.created",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionCreated(
            @Payload java.util.Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        SubscriptionCreatedEvent event = null;
        try {
            // Convert Map to SubscriptionCreatedEvent
            event = mapToSubscriptionCreatedEvent(payload);

            log.info("Received SubscriptionCreatedEvent for company: {} from partition: {}, offset: {}",
                    event.getCompanyId(), partition, offset);

            // Validate event data
            if (event.getCompanyId() == null) {
                log.error("SubscriptionCreatedEvent has null companyId, skipping notification creation");
                return;
            }

            // Build welcome message
            String welcomeMessage = buildSubscriptionCreatedMessage(event);

            // Build notification metadata
            String metadata = buildSubscriptionMetadata(
                    event.getSubscriptionId(),
                    event.getPlanType(),
                    event.getPaymentReferenceId()
            );

            // Create notification request
            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.SUBSCRIPTION)
                    .title("🎉 Welcome to Premium!")
                    .message(welcomeMessage)
                    .referenceId(event.getSubscriptionId() != null ? event.getSubscriptionId().toString() : null)
                    .referenceType("SUBSCRIPTION_CREATED")
                    .metadata(metadata)
                    .build();

            // Send notification
            internalNotificationService.createNotification(notification);

            log.info("Successfully created welcome notification for subscription: {} (company: {})",
                    event.getSubscriptionId(), event.getCompanyId());

        } catch (Exception e) {
            log.error("Error processing SubscriptionCreatedEvent for company: {}, subscription: {}, error: {}",
                    event != null ? event.getCompanyId() : "unknown",
                    event != null ? event.getSubscriptionId() : "unknown",
                    e.getMessage(), e);
            // Don't rethrow - we don't want to block the Kafka consumer
            // Consider implementing dead-letter queue for failed events
        }
    }

    /**
     * Handles SubscriptionRenewedEvent to send a renewal confirmation notification.
     *
     * @param event the subscription renewed event
     * @param partition the Kafka partition
     * @param offset the message offset
     */
    @KafkaListener(
            topics = "subscription.renewed",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionRenewed(
            @Payload SubscriptionRenewedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received SubscriptionRenewedEvent for company: {} from partition: {}, offset: {}",
                    event.getCompanyId(), partition, offset);

            if (event.getCompanyId() == null) {
                log.error("SubscriptionRenewedEvent has null companyId, skipping notification creation");
                return;
            }

            String renewalMessage = buildSubscriptionRenewedMessage(event);

            String metadata = buildRenewalMetadata(
                    event.getSubscriptionId(),
                    event.getPlanType(),
                    event.getPaymentReferenceId(),
                    event.getRenewalAmount(),
                    event.getCurrency(),
                    event.getIsAutoRenewal()
            );

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.SUBSCRIPTION)
                    .title("✅ Subscription Renewed Successfully")
                    .message(renewalMessage)
                    .referenceId(event.getSubscriptionId() != null ? event.getSubscriptionId().toString() : null)
                    .referenceType("SUBSCRIPTION_RENEWED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created renewal notification for subscription: {} (company: {})",
                    event.getSubscriptionId(), event.getCompanyId());

        } catch (Exception e) {
            log.error("Error processing SubscriptionRenewedEvent for company: {}, subscription: {}",
                    event.getCompanyId(), event.getSubscriptionId(), e);
        }
    }

    /**
     * Handles SubscriptionExpiredEvent to send an expiration alert notification.
     *
     * @param event the subscription expired event
     * @param partition the Kafka partition
     * @param offset the message offset
     */
    @KafkaListener(
            topics = "subscription.expired",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionExpired(
            @Payload SubscriptionExpiredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received SubscriptionExpiredEvent for company: {} from partition: {}, offset: {}",
                    event.getCompanyId(), partition, offset);

            if (event.getCompanyId() == null) {
                log.error("SubscriptionExpiredEvent has null companyId, skipping notification creation");
                return;
            }

            String expirationMessage = buildSubscriptionExpiredMessage(event);

            String metadata = buildExpirationMetadata(
                    event.getSubscriptionId(),
                    event.getPlanType(),
                    event.getExpirationReason(),
                    event.getHadAutoRenewal()
            );

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.ALERT)
                    .title("⚠️ Subscription Expired")
                    .message(expirationMessage)
                    .referenceId(event.getSubscriptionId() != null ? event.getSubscriptionId().toString() : null)
                    .referenceType("SUBSCRIPTION_EXPIRED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created expiration notification for subscription: {} (company: {})",
                    event.getSubscriptionId(), event.getCompanyId());

        } catch (Exception e) {
            log.error("Error processing SubscriptionExpiredEvent for company: {}, subscription: {}",
                    event.getCompanyId(), event.getSubscriptionId(), e);
        }
    }

    /**
     * Handles SubscriptionCancelledEvent to send a cancellation confirmation notification.
     *
     * @param event the subscription cancelled event
     * @param partition the Kafka partition
     * @param offset the message offset
     */
    @KafkaListener(
            topics = "subscription.cancelled",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionCancelled(
            @Payload SubscriptionCancelledEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received SubscriptionCancelledEvent for company: {} from partition: {}, offset: {}",
                    event.getCompanyId(), partition, offset);

            if (event.getCompanyId() == null) {
                log.error("SubscriptionCancelledEvent has null companyId, skipping notification creation");
                return;
            }

            String cancellationMessage = buildSubscriptionCancelledMessage(event);

            String metadata = buildCancellationMetadata(
                    event.getSubscriptionId(),
                    event.getPlanType(),
                    event.getCancellationReason(),
                    event.getIsImmediateCancellation(),
                    event.getCancelledByType()
            );

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.SUBSCRIPTION)
                    .title("Subscription Cancelled")
                    .message(cancellationMessage)
                    .referenceId(event.getSubscriptionId() != null ? event.getSubscriptionId().toString() : null)
                    .referenceType("SUBSCRIPTION_CANCELLED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created cancellation notification for subscription: {} (company: {})",
                    event.getSubscriptionId(), event.getCompanyId());

        } catch (Exception e) {
            log.error("Error processing SubscriptionCancelledEvent for company: {}, subscription: {}",
                    event.getCompanyId(), event.getSubscriptionId(), e);
        }
    }

    /**
     * Handles SubscriptionExpiringSoonEvent to send a renewal reminder notification.
     *
     * @param event the subscription expiring soon event
     * @param partition the Kafka partition
     * @param offset the message offset
     */
    @KafkaListener(
            topics = "subscription.expiring-soon",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionExpiringSoon(
            @Payload SubscriptionExpiringSoonEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received SubscriptionExpiringSoonEvent for company: {} (days remaining: {}) from partition: {}, offset: {}",
                    event.getCompanyId(), event.getDaysRemaining(), partition, offset);

            if (event.getCompanyId() == null) {
                log.error("SubscriptionExpiringSoonEvent has null companyId, skipping notification creation");
                return;
            }

            String reminderMessage = buildSubscriptionExpiringSoonMessage(event);

            String metadata = buildExpiringMetadata(
                    event.getSubscriptionId(),
                    event.getPlanType(),
                    event.getDaysRemaining(),
                    event.getUrgencyLevel(),
                    event.getReminderCount()
            );

            // Determine notification type based on urgency
            NotificationType notificationType = "CRITICAL".equals(event.getUrgencyLevel()) ||
                    "URGENT".equals(event.getUrgencyLevel())
                    ? NotificationType.ALERT
                    : NotificationType.SUBSCRIPTION;

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(notificationType)
                    .title(buildExpiringTitle(event.getDaysRemaining(), event.getUrgencyLevel()))
                    .message(reminderMessage)
                    .referenceId(event.getSubscriptionId() != null ? event.getSubscriptionId().toString() : null)
                    .referenceType("SUBSCRIPTION_EXPIRING_SOON")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created expiring reminder notification for subscription: {} (company: {}, reminder #{})",
                    event.getSubscriptionId(), event.getCompanyId(), event.getReminderCount());

        } catch (Exception e) {
            log.error("Error processing SubscriptionExpiringSoonEvent for company: {}, subscription: {}",
                    event.getCompanyId(), event.getSubscriptionId(), e);
        }
    }

    // ========== Message Building Helper Methods ==========

    /**
     * Builds a welcome message for subscription creation.
     */
    private String buildSubscriptionCreatedMessage(SubscriptionCreatedEvent event) {
        StringBuilder message = new StringBuilder();
        message.append("Congratulations! Your premium subscription has been activated successfully. ");

        if (event.getPlanType() != null) {
            message.append("You are now on the ").append(event.getPlanType()).append(" plan. ");
        }

        message.append("\n\nPremium Features Unlocked:\n");
        message.append("• Unlimited job postings\n");
        message.append("• Advanced applicant search and filtering\n");
        message.append("• Priority customer support\n");
        message.append("• Detailed analytics and insights\n");
        message.append("• Featured company profile\n");

        if (event.getEndAt() != null) {
            message.append("\n\nYour subscription is active until ")
                    .append(event.getEndAt().format(DATE_FORMATTER))
                    .append(".");
        } else {
            message.append("\n\nYou have lifetime access to all premium features!");
        }

        message.append("\n\nThank you for choosing our premium service. We're excited to help you find the perfect candidates!");

        return message.toString();
    }

    /**
     * Builds a renewal confirmation message.
     */
    private String buildSubscriptionRenewedMessage(SubscriptionRenewedEvent event) {
        StringBuilder message = new StringBuilder();

        if (Boolean.TRUE.equals(event.getIsAutoRenewal())) {
            message.append("Your premium subscription has been automatically renewed. ");
        } else {
            message.append("Your premium subscription has been successfully renewed. ");
        }

        message.append("Thank you for continuing with us!\n\n");

        if (event.getNewEndAt() != null) {
            message.append("Your subscription is now active until ")
                    .append(event.getNewEndAt().format(DATE_FORMATTER))
                    .append(".\n\n");
        }

        if (event.getRenewalAmount() != null && event.getCurrency() != null) {
            message.append(String.format("Amount charged: %.2f %s\n", event.getRenewalAmount(), event.getCurrency()));
        }

        if (event.getPaymentReferenceId() != null) {
            message.append("Payment reference: ").append(event.getPaymentReferenceId()).append("\n");
        }

        message.append("\nYou continue to have access to all premium features. Happy recruiting!");

        return message.toString();
    }

    /**
     * Builds an expiration alert message.
     */
    private String buildSubscriptionExpiredMessage(SubscriptionExpiredEvent event) {
        StringBuilder message = new StringBuilder();
        message.append("Your premium subscription has expired. ");

        // Provide context based on expiration reason
        if (event.isPaymentFailure()) {
            message.append("We were unable to process your payment for renewal. ");
            message.append("Please update your payment information to reactivate your premium subscription.\n\n");
        } else if (event.wasCancelled()) {
            message.append("Your subscription was previously cancelled and has now ended.\n\n");
        } else {
            message.append("Your subscription period has ended.\n\n");
        }

        message.append("What this means:\n");
        message.append("• Your job postings may be limited or unpublished\n");
        message.append("• Access to advanced features is restricted\n");
        message.append("• Premium support is no longer available\n\n");

        message.append("Don't let great candidates slip away! Renew your subscription now to:");
        message.append("\n• Regain access to all premium features");
        message.append("\n• Continue receiving quality applications");
        message.append("\n• Maintain your competitive edge in hiring");

        message.append("\n\nClick here to renew and get back to finding top talent!");

        return message.toString();
    }

    /**
     * Builds a cancellation confirmation message.
     */
    private String buildSubscriptionCancelledMessage(SubscriptionCancelledEvent event) {
        StringBuilder message = new StringBuilder();

        message.append("We've received your subscription cancellation request");

        if (event.wasCancelledByAdmin()) {
            message.append(" (processed by admin)");
        } else if (event.wasCancelledBySystem()) {
            message.append(" (automated cancellation)");
        }

        message.append(".\n\n");

        message.append(event.getCancellationMessage());

        message.append("\n\nYou can reactivate your premium subscription at any time.");
        message.append("\n\nWe're sorry to see you go. If you have any feedback about your experience, please let us know so we can improve our service.");

        if (Boolean.TRUE.equals(event.getWillProcessRefund()) && event.getRefundAmount() != null) {
            message.append(String.format("\n\nA refund of %.2f %s will be processed within 5-7 business days.",
                    event.getRefundAmount(), event.getCurrency()));
        }

        return message.toString();
    }

    /**
     * Builds an expiring soon reminder message.
     */
    private String buildSubscriptionExpiringSoonMessage(SubscriptionExpiringSoonEvent event) {
        StringBuilder message = new StringBuilder();

        message.append(event.getUrgencyMessage());
        message.append("\n\n");

        if (event.getExpiresAt() != null) {
            message.append("Expiration date: ").append(event.getExpiresAt().format(DATETIME_FORMATTER)).append("\n\n");
        }

        if (Boolean.FALSE.equals(event.getHasAutoRenewal())) {
            message.append("⚠️ Auto-renewal is NOT enabled. You must renew manually to avoid service interruption.\n\n");
        } else {
            message.append("✅ Auto-renewal is enabled. Your subscription will renew automatically if your payment method is valid.\n\n");
        }

        message.append(event.getRenewalCallToAction());

        if (event.hasSpecialOffer()) {
            message.append("\n\n").append(event.getSpecialOfferMessage());
        }

        if (event.getRenewalPrice() != null && event.getCurrency() != null) {
            message.append(String.format("\n\nRenewal price: %.2f %s", event.getRenewalPrice(), event.getCurrency()));
        }

        return message.toString();
    }

    /**
     * Builds a title for expiring subscription notification.
     */
    private String buildExpiringTitle(Integer daysRemaining, String urgencyLevel) {
        if (daysRemaining == null) {
            return "Subscription Expiring Soon";
        }

        if (daysRemaining == 0) {
            return "⚠️ Subscription Expires TODAY!";
        } else if (daysRemaining == 1) {
            return "⚠️ Subscription Expires Tomorrow!";
        } else if ("URGENT".equals(urgencyLevel) || "CRITICAL".equals(urgencyLevel)) {
            return String.format("⚠️ Subscription Expires in %d Days", daysRemaining);
        } else {
            return String.format("Subscription Reminder: %d Days Remaining", daysRemaining);
        }
    }

    // ========== Metadata Building Helper Methods ==========

    private String buildSubscriptionMetadata(Object subscriptionId, String planType, String paymentRef) {
        return String.format("{\"subscriptionId\":\"%s\",\"planType\":\"%s\",\"paymentReference\":\"%s\"}",
                subscriptionId, planType, paymentRef);
    }

    private String buildRenewalMetadata(Object subscriptionId, String planType, String paymentRef,
                                        Double amount, String currency, Boolean isAutoRenewal) {
        return String.format("{\"subscriptionId\":\"%s\",\"planType\":\"%s\",\"paymentReference\":\"%s\"," +
                        "\"amount\":%.2f,\"currency\":\"%s\",\"isAutoRenewal\":%b}",
                subscriptionId, planType, paymentRef, amount != null ? amount : 0.0, currency, isAutoRenewal);
    }

    private String buildExpirationMetadata(Object subscriptionId, String planType, String reason, Boolean hadAutoRenewal) {
        return String.format("{\"subscriptionId\":\"%s\",\"planType\":\"%s\",\"expirationReason\":\"%s\"," +
                        "\"hadAutoRenewal\":%b}",
                subscriptionId, planType, reason, hadAutoRenewal);
    }

    private String buildCancellationMetadata(Object subscriptionId, String planType, String reason,
                                             Boolean isImmediate, String cancelledBy) {
        return String.format("{\"subscriptionId\":\"%s\",\"planType\":\"%s\",\"cancellationReason\":\"%s\"," +
                        "\"isImmediate\":%b,\"cancelledBy\":\"%s\"}",
                subscriptionId, planType, reason, isImmediate, cancelledBy);
    }

    private String buildExpiringMetadata(Object subscriptionId, String planType, Integer daysRemaining,
                                         String urgency, Integer reminderCount) {
        return String.format("{\"subscriptionId\":\"%s\",\"planType\":\"%s\",\"daysRemaining\":%d," +
                        "\"urgencyLevel\":\"%s\",\"reminderCount\":%d}",
                subscriptionId, planType, daysRemaining, urgency, reminderCount);
    }

    /**
     * Helper method to convert Map payload to SubscriptionCreatedEvent.
     * Handles LocalDateTime deserialization from array format.
     */
    private SubscriptionCreatedEvent mapToSubscriptionCreatedEvent(java.util.Map<String, Object> payload) {
        return SubscriptionCreatedEvent.builder()
                .subscriptionId(java.util.UUID.fromString(payload.get("subscriptionId").toString()))
                .companyId(java.util.UUID.fromString(payload.get("companyId").toString()))
                .planType((String) payload.get("planType"))
                .startAt(parseLocalDateTime(payload.get("startAt")))
                .endAt(parseLocalDateTime(payload.get("endAt")))
                .paymentReferenceId((String) payload.get("paymentReferenceId"))
                .eventTimestamp(parseLocalDateTime(payload.get("eventTimestamp")))
                .eventSource((String) payload.get("eventSource"))
                .build();
    }

    /**
     * Parse LocalDateTime from either array format or string format.
     */
    @SuppressWarnings("unchecked")
    private java.time.LocalDateTime parseLocalDateTime(Object dateTimeObj) {
        if (dateTimeObj == null) {
            return null;
        }
        if (dateTimeObj instanceof java.util.List) {
            // Handle array format: [2026, 1, 10, 14, 13, 51, 660735000]
            java.util.List<Integer> dateTimeParts = (java.util.List<Integer>) dateTimeObj;
            return java.time.LocalDateTime.of(
                    dateTimeParts.get(0), // year
                    dateTimeParts.get(1), // month
                    dateTimeParts.get(2), // day
                    dateTimeParts.get(3), // hour
                    dateTimeParts.get(4), // minute
                    dateTimeParts.get(5), // second
                    dateTimeParts.size() > 6 ? dateTimeParts.get(6) : 0 // nano
            );
        } else if (dateTimeObj instanceof String) {
            // Handle string format
            return java.time.LocalDateTime.parse((String) dateTimeObj);
        }
        return null;
    }
}

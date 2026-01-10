package com.devision.job_manager_notification.service.internal;

import com.devision.job_manager_notification.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal service interface for notification event tracking and auditing.
 * Records all notification lifecycle events for compliance and debugging.
 */
public interface NotificationEventService {

    /**
     * Records a notification creation event.
     *
     * @param notificationId the notification ID
     * @param userId the user ID
     * @param type the notification type
     * @param metadata additional event metadata
     */
    void recordCreationEvent(UUID notificationId, UUID userId, NotificationType type, Map<String, Object> metadata);

    /**
     * Records a notification delivery event.
     *
     * @param notificationId the notification ID
     * @param channel the delivery channel
     * @param deliveredAt the delivery timestamp
     * @param metadata additional event metadata
     */
    void recordDeliveryEvent(UUID notificationId, String channel, LocalDateTime deliveredAt, Map<String, Object> metadata);

    /**
     * Records a notification read event.
     *
     * @param notificationId the notification ID
     * @param userId the user ID
     * @param readAt the read timestamp
     */
    void recordReadEvent(UUID notificationId, UUID userId, LocalDateTime readAt);

    /**
     * Records a notification click event.
     *
     * @param notificationId the notification ID
     * @param userId the user ID
     * @param clickedAt the click timestamp
     * @param actionId the ID of the action clicked (if applicable)
     */
    void recordClickEvent(UUID notificationId, UUID userId, LocalDateTime clickedAt, String actionId);

    /**
     * Records a notification dismissal event.
     *
     * @param notificationId the notification ID
     * @param userId the user ID
     * @param dismissedAt the dismissal timestamp
     */
    void recordDismissalEvent(UUID notificationId, UUID userId, LocalDateTime dismissedAt);

    /**
     * Records a notification failure event.
     *
     * @param notificationId the notification ID
     * @param channel the delivery channel
     * @param errorMessage the error message
     * @param failedAt the failure timestamp
     */
    void recordFailureEvent(UUID notificationId, String channel, String errorMessage, LocalDateTime failedAt);

    /**
     * Records a notification retry event.
     *
     * @param notificationId the notification ID
     * @param channel the delivery channel
     * @param retryCount the retry attempt number
     * @param retriedAt the retry timestamp
     */
    void recordRetryEvent(UUID notificationId, String channel, int retryCount, LocalDateTime retriedAt);

    /**
     * Records a notification expiration event.
     *
     * @param notificationId the notification ID
     * @param expiredAt the expiration timestamp
     */
    void recordExpirationEvent(UUID notificationId, LocalDateTime expiredAt);

    /**
     * Records a notification deletion event.
     *
     * @param notificationId the notification ID
     * @param userId the user ID who deleted it
     * @param deletedAt the deletion timestamp
     */
    void recordDeletionEvent(UUID notificationId, UUID userId, LocalDateTime deletedAt);

    /**
     * Records a preference change event.
     *
     * @param userId the user ID
     * @param preferenceType the type of preference changed
     * @param oldValue the old value
     * @param newValue the new value
     * @param changedAt the change timestamp
     */
    void recordPreferenceChangeEvent(UUID userId, String preferenceType, String oldValue, String newValue, LocalDateTime changedAt);

    /**
     * Records a template usage event.
     *
     * @param templateId the template ID
     * @param notificationId the notification ID that used it
     * @param usedAt the usage timestamp
     */
    void recordTemplateUsageEvent(UUID templateId, UUID notificationId, LocalDateTime usedAt);

    /**
     * Records a batch operation event.
     *
     * @param batchId the batch operation ID
     * @param operationType the type of operation
     * @param notificationCount the number of notifications in batch
     * @param startedAt the start timestamp
     * @param completedAt the completion timestamp
     */
    void recordBatchOperationEvent(UUID batchId, String operationType, int notificationCount,
                                   LocalDateTime startedAt, LocalDateTime completedAt);

    /**
     * Gets all events for a notification.
     *
     * @param notificationId the notification ID
     * @return list of event records
     */
    List<Map<String, Object>> getNotificationEvents(UUID notificationId);

    /**
     * Gets events for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of event records
     */
    List<Map<String, Object>> getUserEvents(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets events by type within a date range.
     *
     * @param eventType the event type
     * @param startDate the start date
     * @param endDate the end date
     * @return list of event records
     */
    List<Map<String, Object>> getEventsByType(String eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets delivery success rate from events.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return success rate as percentage
     */
    double getDeliverySuccessRate(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets average time to delivery from events.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return average time in milliseconds
     */
    long getAverageTimeToDelivery(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets average time to read from events.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return average time in milliseconds
     */
    long getAverageTimeToRead(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets event count by type.
     *
     * @param eventType the event type
     * @param startDate the start date
     * @param endDate the end date
     * @return event count
     */
    long getEventCount(String eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Archives old events to cold storage.
     *
     * @param olderThanDays archive events older than this many days
     * @return number of events archived
     */
    long archiveOldEvents(int olderThanDays);

    /**
     * Purges archived events.
     *
     * @param olderThanDays purge events older than this many days
     * @return number of events purged
     */
    long purgeArchivedEvents(int olderThanDays);

    /**
     * Exports events to CSV format.
     *
     * @param notificationId the notification ID
     * @return CSV content
     */
    String exportEventsToCSV(UUID notificationId);

    /**
     * Exports events for audit report.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return formatted audit report
     */
    String generateAuditReport(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Records a custom event.
     *
     * @param eventType the custom event type
     * @param eventData the event data
     */
    void recordCustomEvent(String eventType, Map<String, Object> eventData);

    /**
     * Gets event statistics summary.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return statistics summary
     */
    Map<String, Object> getEventStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Records a security event (suspicious activity).
     *
     * @param userId the user ID
     * @param eventType the security event type
     * @param details the event details
     * @param timestamp the event timestamp
     */
    void recordSecurityEvent(UUID userId, String eventType, String details, LocalDateTime timestamp);

    /**
     * Gets security events for investigation.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of security event records
     */
    List<Map<String, Object>> getSecurityEvents(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Records a compliance-related event.
     *
     * @param eventType the compliance event type
     * @param userId the user ID
     * @param details the event details
     * @param timestamp the event timestamp
     */
    void recordComplianceEvent(String eventType, UUID userId, String details, LocalDateTime timestamp);

    /**
     * Gets compliance events for reporting.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of compliance event records
     */
    List<Map<String, Object>> getComplianceEvents(LocalDateTime startDate, LocalDateTime endDate);
}

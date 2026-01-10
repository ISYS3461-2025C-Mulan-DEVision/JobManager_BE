package com.devision.job_manager_notification.service.internal;

import com.devision.job_manager_notification.enums.NotificationType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal service interface for validating notifications.
 * Ensures notification data integrity and compliance with business rules.
 */
public interface NotificationValidationService {

    /**
     * Validates a notification before creation.
     *
     * @param userId the user ID
     * @param type the notification type
     * @param title the notification title
     * @param message the notification message
     * @return list of validation errors, empty if valid
     */
    List<String> validateNotificationData(UUID userId, NotificationType type, String title, String message);

    /**
     * Validates notification title length and format.
     *
     * @param title the title to validate
     * @return validation error message, or null if valid
     */
    String validateTitle(String title);

    /**
     * Validates notification message content.
     *
     * @param message the message to validate
     * @return validation error message, or null if valid
     */
    String validateMessage(String message);

    /**
     * Validates user ID exists and is active.
     *
     * @param userId the user ID to validate
     * @return validation error message, or null if valid
     */
    String validateUserId(UUID userId);

    /**
     * Validates notification type is supported.
     *
     * @param type the notification type
     * @return validation error message, or null if valid
     */
    String validateNotificationType(NotificationType type);

    /**
     * Validates reference ID format.
     *
     * @param referenceId the reference ID
     * @return validation error message, or null if valid
     */
    String validateReferenceId(String referenceId);

    /**
     * Validates reference type is recognized.
     *
     * @param referenceType the reference type
     * @return validation error message, or null if valid
     */
    String validateReferenceType(String referenceType);

    /**
     * Validates notification metadata.
     *
     * @param metadata the metadata map
     * @return list of validation errors, empty if valid
     */
    List<String> validateMetadata(Map<String, String> metadata);

    /**
     * Validates scheduled delivery time.
     *
     * @param scheduledTime the scheduled time (ISO-8601 format)
     * @return validation error message, or null if valid
     */
    String validateScheduledTime(String scheduledTime);

    /**
     * Validates notification priority value.
     *
     * @param priority the priority (1-10)
     * @return validation error message, or null if valid
     */
    String validatePriority(int priority);

    /**
     * Validates expiration time for notification.
     *
     * @param expiresAt the expiration timestamp
     * @return validation error message, or null if valid
     */
    String validateExpirationTime(String expiresAt);

    /**
     * Checks if user has reached notification rate limit.
     *
     * @param userId the user ID
     * @param type the notification type
     * @return true if rate limit exceeded
     */
    boolean isRateLimitExceeded(UUID userId, NotificationType type);

    /**
     * Checks if notification content contains spam indicators.
     *
     * @param title the notification title
     * @param message the notification message
     * @return true if spam detected
     */
    boolean isSpam(String title, String message);

    /**
     * Validates notification content against profanity filter.
     *
     * @param content the content to check
     * @return true if profanity detected
     */
    boolean containsProfanity(String content);

    /**
     * Validates URL links in notification content.
     *
     * @param content the content containing URLs
     * @return list of invalid URLs, empty if all valid
     */
    List<String> validateUrls(String content);

    /**
     * Checks if notification is duplicate within time window.
     *
     * @param userId the user ID
     * @param type the notification type
     * @param message the notification message
     * @param windowMinutes the time window in minutes
     * @return true if duplicate detected
     */
    boolean isDuplicate(UUID userId, NotificationType type, String message, int windowMinutes);

    /**
     * Validates batch notification request.
     *
     * @param userIds list of user IDs
     * @param type the notification type
     * @param title the title
     * @param message the message
     * @return list of validation errors, empty if valid
     */
    List<String> validateBatchNotification(List<UUID> userIds, NotificationType type, String title, String message);

    /**
     * Validates notification template variables.
     *
     * @param templateId the template ID
     * @param variables the variables to substitute
     * @return list of validation errors, empty if valid
     */
    List<String> validateTemplateVariables(UUID templateId, Map<String, Object> variables);

    /**
     * Validates user consent for notification type.
     *
     * @param userId the user ID
     * @param type the notification type
     * @return true if user has consented
     */
    boolean hasUserConsent(UUID userId, NotificationType type);

    /**
     * Validates notification size is within limits.
     *
     * @param title the title
     * @param message the message
     * @param metadata the metadata
     * @return validation error message, or null if valid
     */
    String validateSize(String title, String message, Map<String, String> metadata);

    /**
     * Validates character encoding is supported.
     *
     * @param content the content to validate
     * @return validation error message, or null if valid
     */
    String validateEncoding(String content);

    /**
     * Validates notification attachment if present.
     *
     * @param attachmentUrl the attachment URL
     * @param attachmentType the attachment MIME type
     * @return validation error message, or null if valid
     */
    String validateAttachment(String attachmentUrl, String attachmentType);

    /**
     * Validates notification action buttons.
     *
     * @param actions the list of action configurations
     * @return list of validation errors, empty if valid
     */
    List<String> validateActions(List<Map<String, String>> actions);

    /**
     * Validates notification category.
     *
     * @param category the notification category
     * @return validation error message, or null if valid
     */
    String validateCategory(String category);

    /**
     * Validates notification tags.
     *
     * @param tags the list of tags
     * @return list of validation errors, empty if valid
     */
    List<String> validateTags(List<String> tags);

    /**
     * Checks if user is in quiet hours.
     *
     * @param userId the user ID
     * @return true if in quiet hours
     */
    boolean isInQuietHours(UUID userId);

    /**
     * Validates localization parameters.
     *
     * @param languageCode the language code
     * @param localizationKey the localization key
     * @return validation error message, or null if valid
     */
    String validateLocalization(String languageCode, String localizationKey);

    /**
     * Performs comprehensive validation of all notification fields.
     *
     * @param notificationData the complete notification data
     * @return comprehensive validation report
     */
    Map<String, List<String>> performComprehensiveValidation(Map<String, Object> notificationData);

    /**
     * Validates notification against custom business rules.
     *
     * @param notificationData the notification data
     * @param ruleSetName the name of the rule set to apply
     * @return list of validation errors, empty if valid
     */
    List<String> validateAgainstBusinessRules(Map<String, Object> notificationData, String ruleSetName);

    /**
     * Sanitizes notification content to prevent XSS.
     *
     * @param content the content to sanitize
     * @return sanitized content
     */
    String sanitizeContent(String content);

    /**
     * Validates notification complies with GDPR requirements.
     *
     * @param userId the user ID
     * @param type the notification type
     * @param data the notification data
     * @return list of GDPR compliance issues, empty if compliant
     */
    List<String> validateGDPRCompliance(UUID userId, NotificationType type, Map<String, Object> data);
}

package com.devision.job_manager_notification.listener;

import com.devision.job_manager_notification.dto.internal.InternalCreateNotificationRequest;
import com.devision.job_manager_notification.enums.NotificationType;
import com.devision.job_manager_notification.service.InternalNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka listener for job post related events.
 * Listens to job post topics to notify companies about their job post lifecycle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobPostEventListener {

    private final InternalNotificationService internalNotificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    /**
     * Handles job post created events.
     * Notifies company when a new job post is created.
     */
    @KafkaListener(
            topics = "jobpost.created",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostCreated(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostCreatedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostCreatedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostCreatedMessage(title);
            String metadata = buildJobPostMetadata(jobPostId, title, "CREATED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Created Successfully")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_CREATED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post created: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostCreatedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post published events.
     * Notifies company when their job post goes live.
     */
    @KafkaListener(
            topics = "jobpost.published",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostPublished(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostPublishedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");
            Object publishedAtObj = payload.get("publishedAt");
            Object expiryAtObj = payload.get("expiryAt");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostPublishedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            LocalDateTime publishedAt = parseLocalDateTime(publishedAtObj);
            LocalDateTime expiryAt = parseLocalDateTime(expiryAtObj);

            String message = buildJobPostPublishedMessage(title, publishedAt, expiryAt);
            String metadata = buildJobPostMetadata(jobPostId, title, "PUBLISHED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("🎉 Job Post Published Successfully")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_PUBLISHED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post published: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostPublishedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post updated events.
     * Notifies company when their job post is updated.
     */
    @KafkaListener(
            topics = "jobpost.updated",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostUpdated(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostUpdatedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostUpdatedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostUpdatedMessage(title);
            String metadata = buildJobPostMetadata(jobPostId, title, "UPDATED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Updated")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_UPDATED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post updated: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostUpdatedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post expired events.
     * Notifies company when their job post expires.
     */
    @KafkaListener(
            topics = "jobpost.expired",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostExpired(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostExpiredEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");
            Object expiredAtObj = payload.get("expiredAt");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostExpiredEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            LocalDateTime expiredAt = parseLocalDateTime(expiredAtObj);

            String message = buildJobPostExpiredMessage(title, expiredAt);
            String metadata = buildJobPostMetadata(jobPostId, title, "EXPIRED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.ALERT)
                    .title("⚠️ Job Post Expired")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_EXPIRED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post expired: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostExpiredEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post unpublished events.
     * Notifies company when their job post is unpublished.
     */
    @KafkaListener(
            topics = "jobpost.unpublished",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostUnpublished(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostUnpublishedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostUnpublishedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostUnpublishedMessage(title);
            String metadata = buildJobPostMetadata(jobPostId, title, "UNPUBLISHED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Unpublished")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_UNPUBLISHED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post unpublished: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostUnpublishedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post deleted events.
     * Notifies company when their job post is deleted.
     */
    @KafkaListener(
            topics = "jobpost.deleted",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostDeleted(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostDeletedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostDeletedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostDeletedMessage(title);
            String metadata = buildJobPostMetadata(jobPostId, title, "DELETED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Deleted")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_DELETED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post deleted: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostDeletedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post skills changed events.
     * CRITICAL for Ultimo 4.3.1: Enables instant applicant matching when job requirements change.
     */
    @KafkaListener(
            topics = "jobpost.skills.changed",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostSkillsChanged(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostSkillsChangedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostSkillsChangedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostSkillsChangedMessage(title);
            String metadata = buildJobPostMetadata(jobPostId, title, "SKILLS_CHANGED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Skills Updated")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_SKILLS_CHANGED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post skills changed: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostSkillsChangedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    /**
     * Handles job post country changed events.
     * CRITICAL for Ultimo 4.3.1: Enables instant applicant matching when location requirements change.
     */
    @KafkaListener(
            topics = "jobpost.country.changed",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleJobPostCountryChanged(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("Received JobPostCountryChangedEvent from partition: {}, offset: {}", partition, offset);
            log.debug("Payload: {}", payload);

            UUID companyId = parseUUID(payload.get("companyId"));
            UUID jobPostId = parseUUID(payload.get("jobPostId"));
            String title = (String) payload.get("title");
            String previousCountryCode = (String) payload.get("previousCountryCode");
            String newCountryCode = (String) payload.get("newCountryCode");

            if (companyId == null || jobPostId == null) {
                log.error("Missing required fields in JobPostCountryChangedEvent. CompanyId: {}, JobPostId: {}",
                        companyId, jobPostId);
                return;
            }

            String message = buildJobPostCountryChangedMessage(title, previousCountryCode, newCountryCode);
            String metadata = buildJobPostMetadata(jobPostId, title, "COUNTRY_CHANGED");

            InternalCreateNotificationRequest notification = InternalCreateNotificationRequest.builder()
                    .userId(companyId)
                    .type(NotificationType.SYSTEM)
                    .title("Job Post Location Updated")
                    .message(message)
                    .referenceId(jobPostId.toString())
                    .referenceType("JOB_POST_COUNTRY_CHANGED")
                    .metadata(metadata)
                    .build();

            internalNotificationService.createNotification(notification);

            log.info("Successfully created notification for job post country changed: {} (company: {})",
                    jobPostId, companyId);

        } catch (Exception e) {
            log.error("Error processing JobPostCountryChangedEvent from partition: {}, offset: {}",
                    partition, offset, e);
        }
    }

    // ========== Message Building Helper Methods ==========

    private String buildJobPostCreatedMessage(String title) {
        StringBuilder message = new StringBuilder();
        message.append("Your job post \"").append(title != null ? title : "Untitled").append("\" has been created successfully.\n\n");
        message.append("Next steps:\n");
        message.append("• Review and complete all job details\n");
        message.append("• Publish the job post to start receiving applications\n");
        message.append("• Monitor applicant responses from your dashboard");
        return message.toString();
    }

    private String buildJobPostPublishedMessage(String title, LocalDateTime publishedAt, LocalDateTime expiryAt) {
        StringBuilder message = new StringBuilder();
        message.append("Great news! Your job post \"").append(title != null ? title : "Untitled")
                .append("\" is now live and visible to applicants.\n\n");

        if (publishedAt != null) {
            message.append("Published: ").append(publishedAt.format(DATETIME_FORMATTER)).append("\n");
        }

        if (expiryAt != null) {
            message.append("Expires: ").append(expiryAt.format(DATETIME_FORMATTER)).append("\n\n");
        } else {
            message.append("\n");
        }

        message.append("Your job post is now searchable and applicants matching your requirements will be notified.\n\n");
        message.append("What to expect:\n");
        message.append("• You'll receive notifications for new applications\n");
        message.append("• Matching applicants will be alerted about this opportunity\n");
        message.append("• Track application metrics from your dashboard");

        return message.toString();
    }

    private String buildJobPostUpdatedMessage(String title) {
        StringBuilder message = new StringBuilder();
        message.append("Your job post \"").append(title != null ? title : "Untitled")
                .append("\" has been updated successfully.\n\n");
        message.append("The changes are now reflected in the live posting.");
        return message.toString();
    }

    private String buildJobPostExpiredMessage(String title, LocalDateTime expiredAt) {
        StringBuilder message = new StringBuilder();
        message.append("Your job post \"").append(title != null ? title : "Untitled")
                .append("\" has expired and is no longer visible to applicants.\n\n");

        if (expiredAt != null) {
            message.append("Expired on: ").append(expiredAt.format(DATE_FORMATTER)).append("\n\n");
        }

        message.append("What you can do:\n");
        message.append("• Renew this job post to make it active again\n");
        message.append("• Create a new job post with updated requirements\n");
        message.append("• Review applications received during the active period");

        return message.toString();
    }

    private String buildJobPostUnpublishedMessage(String title) {
        StringBuilder message = new StringBuilder();
        message.append("Your job post \"").append(title != null ? title : "Untitled")
                .append("\" has been unpublished and is no longer visible to applicants.\n\n");
        message.append("You can republish it anytime from your dashboard.");
        return message.toString();
    }

    private String buildJobPostDeletedMessage(String title) {
        StringBuilder message = new StringBuilder();
        message.append("Your job post \"").append(title != null ? title : "Untitled")
                .append("\" has been permanently deleted.\n\n");
        message.append("This action cannot be undone. All associated data has been removed.");
        return message.toString();
    }

    private String buildJobPostSkillsChangedMessage(String title) {
        StringBuilder message = new StringBuilder();
        message.append("The required skills for your job post \"")
                .append(title != null ? title : "Untitled")
                .append("\" have been updated.\n\n");
        message.append("We're now matching your updated requirements with applicants who have the relevant skills.");
        return message.toString();
    }

    private String buildJobPostCountryChangedMessage(String title, String previousCountryCode, String newCountryCode) {
        StringBuilder message = new StringBuilder();
        message.append("The location for your job post \"")
                .append(title != null ? title : "Untitled")
                .append("\" has been updated");

        if (previousCountryCode != null && newCountryCode != null) {
            message.append(" from ").append(previousCountryCode).append(" to ").append(newCountryCode);
        }

        message.append(".\n\n");
        message.append("We're now matching your job post with applicants in the updated location.");
        return message.toString();
    }

    private String buildJobPostMetadata(UUID jobPostId, String title, String action) {
        return String.format("{\"jobPostId\":\"%s\",\"title\":\"%s\",\"action\":\"%s\",\"timestamp\":\"%s\"}",
                jobPostId, sanitizeJsonString(title), action, LocalDateTime.now());
    }

    // ========== Utility Helper Methods ==========

    private UUID parseUUID(Object uuidObj) {
        if (uuidObj == null) {
            return null;
        }
        try {
            if (uuidObj instanceof String) {
                return UUID.fromString((String) uuidObj);
            } else if (uuidObj instanceof UUID) {
                return (UUID) uuidObj;
            } else {
                return UUID.fromString(uuidObj.toString());
            }
        } catch (IllegalArgumentException e) {
            log.error("Failed to parse UUID. Value: {}", uuidObj, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private LocalDateTime parseLocalDateTime(Object dateTimeObj) {
        if (dateTimeObj == null) {
            return null;
        }
        try {
            if (dateTimeObj instanceof java.util.List) {
                java.util.List<Integer> dateTimeParts = (java.util.List<Integer>) dateTimeObj;
                if (dateTimeParts.size() < 3) {
                    log.error("Invalid date array format. Expected at least [year, month, day], got: {}", dateTimeParts);
                    return null;
                }
                int year = dateTimeParts.get(0);
                int month = dateTimeParts.get(1);
                int day = dateTimeParts.get(2);
                int hour = dateTimeParts.size() > 3 ? dateTimeParts.get(3) : 0;
                int minute = dateTimeParts.size() > 4 ? dateTimeParts.get(4) : 0;
                int second = dateTimeParts.size() > 5 ? dateTimeParts.get(5) : 0;
                int nano = dateTimeParts.size() > 6 ? dateTimeParts.get(6) : 0;
                return LocalDateTime.of(year, month, day, hour, minute, second, nano);
            } else if (dateTimeObj instanceof String) {
                return LocalDateTime.parse((String) dateTimeObj);
            } else if (dateTimeObj instanceof LocalDateTime) {
                return (LocalDateTime) dateTimeObj;
            }
        } catch (Exception e) {
            log.error("Failed to parse LocalDateTime. Value: {}. Using null.", dateTimeObj, e);
        }
        return null;
    }

    private String sanitizeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

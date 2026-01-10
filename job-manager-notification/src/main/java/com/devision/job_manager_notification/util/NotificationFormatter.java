package com.devision.job_manager_notification.util;

import com.devision.job_manager_notification.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for formatting notification content and metadata.
 * Provides consistent formatting across different notification types and channels.
 */
@Component
@Slf4j
public class NotificationFormatter {

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formats a notification title based on type.
     *
     * @param type the notification type
     * @param rawTitle the raw title
     * @return formatted title
     */
    public String formatTitle(NotificationType type, String rawTitle) {
        log.debug("Formatting title for type: {}", type);

        try {
            if (rawTitle == null || rawTitle.trim().isEmpty()) {
                log.warn("Raw title is null or empty");
                return "";
            }

            if (type == null) {
                log.warn("Notification type is null, using raw title");
                return rawTitle.trim();
            }

            // Add emoji prefix based on type
            String prefix = getEmojiForType(type);
            String formatted = prefix + " " + rawTitle.trim();

            log.debug("Formatted title: {}", formatted);
            return formatted;

        } catch (Exception e) {
            log.error("Error formatting title", e);
            return rawTitle != null ? rawTitle : "";
        }
    }

    /**
     * Formats a notification message for display.
     *
     * @param message the raw message
     * @param maxLength the maximum length
     * @return formatted message
     */
    public String formatMessage(String message, int maxLength) {
        log.debug("Formatting message with max length: {}", maxLength);

        try {
            if (message == null || message.trim().isEmpty()) {
                log.warn("Message is null or empty");
                return "";
            }

            String trimmed = message.trim();

            if (trimmed.length() <= maxLength) {
                return trimmed;
            }

            // Truncate and add ellipsis
            String truncated = trimmed.substring(0, maxLength - 3) + "...";
            log.debug("Truncated message from {} to {} characters", message.length(), truncated.length());

            return truncated;

        } catch (Exception e) {
            log.error("Error formatting message", e);
            return message != null ? message : "";
        }
    }

    /**
     * Formats a timestamp for notification display.
     *
     * @param timestamp the timestamp
     * @return formatted timestamp string
     */
    public String formatTimestamp(LocalDateTime timestamp) {
        log.debug("Formatting timestamp: {}", timestamp);

        try {
            if (timestamp == null) {
                log.warn("Timestamp is null");
                return "";
            }

            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(timestamp, now);

            // If less than 1 minute ago
            if (duration.toMinutes() < 1) {
                return "Just now";
            }

            // If less than 1 hour ago
            if (duration.toHours() < 1) {
                long minutes = duration.toMinutes();
                return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
            }

            // If less than 24 hours ago
            if (duration.toHours() < 24) {
                long hours = duration.toHours();
                return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
            }

            // If less than 7 days ago
            if (duration.toDays() < 7) {
                long days = duration.toDays();
                return days + " day" + (days == 1 ? "" : "s") + " ago";
            }

            // Otherwise, use formatted date
            return timestamp.format(SHORT_DATE_FORMATTER);

        } catch (Exception e) {
            log.error("Error formatting timestamp", e);
            return "";
        }
    }

    /**
     * Formats a full date and time.
     *
     * @param dateTime the date time
     * @return formatted date time string
     */
    public String formatDateTime(LocalDateTime dateTime) {
        log.debug("Formatting date time: {}", dateTime);

        try {
            if (dateTime == null) {
                log.warn("Date time is null");
                return "";
            }

            return dateTime.format(DEFAULT_DATE_FORMATTER);

        } catch (Exception e) {
            log.error("Error formatting date time", e);
            return "";
        }
    }

    /**
     * Formats metadata as a readable string.
     *
     * @param metadata the metadata map
     * @return formatted metadata string
     */
    public String formatMetadata(Map<String, String> metadata) {
        log.debug("Formatting metadata with {} entries", metadata != null ? metadata.size() : 0);

        try {
            if (metadata == null || metadata.isEmpty()) {
                log.debug("Metadata is null or empty");
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append(": ").append(entry.getValue());
            }

            return sb.toString();

        } catch (Exception e) {
            log.error("Error formatting metadata", e);
            return "";
        }
    }

    /**
     * Formats a reference ID for display.
     *
     * @param referenceId the reference ID
     * @return formatted reference ID
     */
    public String formatReferenceId(String referenceId) {
        log.debug("Formatting reference ID: {}", referenceId);

        try {
            if (referenceId == null || referenceId.trim().isEmpty()) {
                log.warn("Reference ID is null or empty");
                return "";
            }

            String trimmed = referenceId.trim();

            // If it's a UUID, show shortened version
            if (trimmed.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                return trimmed.substring(0, 8) + "...";
            }

            // If it's too long, truncate
            if (trimmed.length() > 20) {
                return trimmed.substring(0, 17) + "...";
            }

            return trimmed;

        } catch (Exception e) {
            log.error("Error formatting reference ID", e);
            return referenceId != null ? referenceId : "";
        }
    }

    /**
     * Formats notification priority as human-readable string.
     *
     * @param priority the priority (1-10)
     * @return formatted priority string
     */
    public String formatPriority(int priority) {
        log.debug("Formatting priority: {}", priority);

        try {
            if (priority >= 8) {
                return "Urgent";
            } else if (priority >= 5) {
                return "High";
            } else if (priority >= 3) {
                return "Normal";
            } else {
                return "Low";
            }

        } catch (Exception e) {
            log.error("Error formatting priority", e);
            return "Normal";
        }
    }

    /**
     * Formats notification status for display.
     *
     * @param status the status
     * @return formatted status string
     */
    public String formatStatus(String status) {
        log.debug("Formatting status: {}", status);

        try {
            if (status == null || status.trim().isEmpty()) {
                log.warn("Status is null or empty");
                return "Unknown";
            }

            // Convert to title case
            String[] words = status.toLowerCase().split("_");
            StringBuilder formatted = new StringBuilder();

            for (String word : words) {
                if (formatted.length() > 0) {
                    formatted.append(" ");
                }
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }

            return formatted.toString();

        } catch (Exception e) {
            log.error("Error formatting status", e);
            return status != null ? status : "Unknown";
        }
    }

    /**
     * Formats notification type for display.
     *
     * @param type the notification type
     * @return formatted type string
     */
    public String formatType(NotificationType type) {
        log.debug("Formatting type: {}", type);

        try {
            if (type == null) {
                log.warn("Type is null");
                return "Unknown";
            }

            return formatStatus(type.name());

        } catch (Exception e) {
            log.error("Error formatting type", e);
            return "Unknown";
        }
    }

    /**
     * Gets emoji prefix for notification type.
     *
     * @param type the notification type
     * @return emoji string
     */
    private String getEmojiForType(NotificationType type) {
        try {
            if (type == null) {
                return "📢";
            }

            switch (type) {
                case ALERT:
                    return "⚠️";
                case ACCOUNT:
                    return "👤";
                case SYSTEM:
                    return "⚙️";
                case SUBSCRIPTION:
                    return "📋";
                case JOB_POST:
                    return "💼";
                case APPLICATION:
                    return "📝";
                case MESSAGE:
                    return "💬";
                case REMINDER:
                    return "⏰";
                default:
                    return "📢";
            }

        } catch (Exception e) {
            log.error("Error getting emoji for type", e);
            return "📢";
        }
    }

    /**
     * Sanitizes notification content to prevent XSS.
     *
     * @param content the content to sanitize
     * @return sanitized content
     */
    public String sanitizeContent(String content) {
        log.debug("Sanitizing content");

        try {
            if (content == null || content.trim().isEmpty()) {
                log.debug("Content is null or empty");
                return "";
            }

            String sanitized = content
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;")
                    .replace("/", "&#x2F;");

            log.debug("Sanitized content");
            return sanitized;

        } catch (Exception e) {
            log.error("Error sanitizing content", e);
            return content != null ? content : "";
        }
    }

    /**
     * Truncates text to specified length with ellipsis.
     *
     * @param text the text to truncate
     * @param maxLength the maximum length
     * @return truncated text
     */
    public String truncate(String text, int maxLength) {
        log.debug("Truncating text to max length: {}", maxLength);

        try {
            if (text == null || text.isEmpty()) {
                return "";
            }

            if (maxLength <= 0) {
                log.warn("Invalid max length: {}", maxLength);
                return text;
            }

            if (text.length() <= maxLength) {
                return text;
            }

            if (maxLength <= 3) {
                return text.substring(0, maxLength);
            }

            return text.substring(0, maxLength - 3) + "...";

        } catch (Exception e) {
            log.error("Error truncating text", e);
            return text != null ? text : "";
        }
    }

    /**
     * Removes HTML tags from content.
     *
     * @param html the HTML content
     * @return plain text
     */
    public String stripHtml(String html) {
        log.debug("Stripping HTML tags from content");

        try {
            if (html == null || html.trim().isEmpty()) {
                return "";
            }

            // Simple regex to remove HTML tags
            String text = html.replaceAll("<[^>]*>", "");

            // Decode common HTML entities
            text = text.replace("&nbsp;", " ")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .replace("&#x27;", "'")
                    .replace("&#x2F;", "/");

            log.debug("Successfully stripped HTML tags");
            return text.trim();

        } catch (Exception e) {
            log.error("Error stripping HTML", e);
            return html != null ? html : "";
        }
    }

    /**
     * Capitalizes first letter of text.
     *
     * @param text the text to capitalize
     * @return capitalized text
     */
    public String capitalize(String text) {
        log.debug("Capitalizing text");

        try {
            if (text == null || text.isEmpty()) {
                return "";
            }

            if (text.length() == 1) {
                return text.toUpperCase();
            }

            return Character.toUpperCase(text.charAt(0)) + text.substring(1);

        } catch (Exception e) {
            log.error("Error capitalizing text", e);
            return text != null ? text : "";
        }
    }
}

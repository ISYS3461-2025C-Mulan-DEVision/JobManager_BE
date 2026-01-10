package com.devision.job_manager_notification.service.external;

import com.devision.job_manager_notification.enums.NotificationType;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * External service interface for notification analytics and metrics.
 * Provides insights into notification engagement and performance.
 */
public interface NotificationAnalyticsService {

    /**
     * Gets delivery rate for a specific notification type.
     *
     * @param type the notification type
     * @param startDate the start date for analysis
     * @param endDate the end date for analysis
     * @return delivery rate as percentage (0-100)
     */
    double getDeliveryRate(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets open rate for notifications.
     *
     * @param type the notification type
     * @param startDate the start date for analysis
     * @param endDate the end date for analysis
     * @return open rate as percentage (0-100)
     */
    double getOpenRate(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets click-through rate for notifications.
     *
     * @param type the notification type
     * @param startDate the start date for analysis
     * @param endDate the end date for analysis
     * @return click-through rate as percentage (0-100)
     */
    double getClickThroughRate(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets total notifications sent in a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return total count of notifications sent
     */
    long getTotalNotificationsSent(LocalDate startDate, LocalDate endDate);

    /**
     * Gets total notifications delivered in a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return total count of notifications delivered
     */
    long getTotalNotificationsDelivered(LocalDate startDate, LocalDate endDate);

    /**
     * Gets total notifications read in a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return total count of notifications read
     */
    long getTotalNotificationsRead(LocalDate startDate, LocalDate endDate);

    /**
     * Gets average time to read notifications.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return average time in milliseconds
     */
    long getAverageTimeToRead(LocalDate startDate, LocalDate endDate);

    /**
     * Gets notification performance by type.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map of notification type to performance metrics
     */
    Map<NotificationType, Map<String, Object>> getPerformanceByType(LocalDate startDate, LocalDate endDate);

    /**
     * Gets peak notification times.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map of hour (0-23) to notification count
     */
    Map<Integer, Long> getPeakNotificationTimes(LocalDate startDate, LocalDate endDate);

    /**
     * Gets user engagement score.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return engagement score (0-100)
     */
    double getUserEngagementScore(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Gets top performing notifications.
     *
     * @param limit the maximum number of results
     * @param startDate the start date
     * @param endDate the end date
     * @return map of notification IDs to engagement scores
     */
    Map<UUID, Double> getTopPerformingNotifications(int limit, LocalDate startDate, LocalDate endDate);

    /**
     * Gets notification trends over time.
     *
     * @param type the notification type
     * @param startDate the start date
     * @param endDate the end date
     * @return map of date to notification count
     */
    Map<LocalDate, Long> getNotificationTrends(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets read rate by notification type.
     *
     * @param type the notification type
     * @param startDate the start date
     * @param endDate the end date
     * @return read rate as percentage (0-100)
     */
    double getReadRate(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets dismissal rate for notifications.
     *
     * @param type the notification type
     * @param startDate the start date
     * @param endDate the end date
     * @return dismissal rate as percentage (0-100)
     */
    double getDismissalRate(NotificationType type, LocalDate startDate, LocalDate endDate);

    /**
     * Gets conversion rate for notifications with specific reference type.
     *
     * @param referenceType the reference type
     * @param startDate the start date
     * @param endDate the end date
     * @return conversion rate as percentage (0-100)
     */
    double getConversionRate(String referenceType, LocalDate startDate, LocalDate endDate);

    /**
     * Generates a comprehensive analytics report.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map containing all analytics data
     */
    Map<String, Object> generateAnalyticsReport(LocalDate startDate, LocalDate endDate);

    /**
     * Exports analytics data to CSV format.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return CSV content as string
     */
    String exportAnalyticsToCSV(LocalDate startDate, LocalDate endDate);

    /**
     * Gets real-time notification statistics.
     *
     * @return map with current statistics
     */
    Map<String, Object> getRealtimeStatistics();

    /**
     * Gets notification action click statistics.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map of action types to click counts
     */
    Map<String, Long> getActionClickStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Gets average notifications per user.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return average count
     */
    double getAverageNotificationsPerUser(LocalDate startDate, LocalDate endDate);

    /**
     * Gets active users count (users who interacted with notifications).
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return active users count
     */
    long getActiveUsersCount(LocalDate startDate, LocalDate endDate);

    /**
     * Gets notification retention rate (users who return after notification).
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return retention rate as percentage (0-100)
     */
    double getRetentionRate(LocalDate startDate, LocalDate endDate);
}

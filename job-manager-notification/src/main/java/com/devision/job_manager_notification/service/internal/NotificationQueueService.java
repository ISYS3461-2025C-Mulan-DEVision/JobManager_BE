package com.devision.job_manager_notification.service.internal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Internal service interface for managing notification queues.
 * Handles queuing, prioritization, and batch processing of notifications.
 */
public interface NotificationQueueService {

    /**
     * Adds a notification to the delivery queue.
     *
     * @param notificationId the notification ID
     * @param priority the priority level (1-10, higher is more urgent)
     * @return true if successfully queued
     */
    boolean enqueue(UUID notificationId, int priority);

    /**
     * Removes a notification from the queue.
     *
     * @param notificationId the notification ID
     * @return true if successfully dequeued
     */
    boolean dequeue(UUID notificationId);

    /**
     * Gets the next notification from the queue for processing.
     *
     * @return notification ID, or null if queue is empty
     */
    UUID getNext();

    /**
     * Gets a batch of notifications from the queue.
     *
     * @param batchSize the number of notifications to retrieve
     * @return list of notification IDs
     */
    List<UUID> getBatch(int batchSize);

    /**
     * Gets the current queue size.
     *
     * @return number of notifications in queue
     */
    long getQueueSize();

    /**
     * Clears all notifications from the queue.
     *
     * @return number of notifications cleared
     */
    long clearQueue();

    /**
     * Requeues a failed notification with updated priority.
     *
     * @param notificationId the notification ID
     * @param newPriority the new priority level
     * @return true if successfully requeued
     */
    boolean requeue(UUID notificationId, int newPriority);

    /**
     * Moves a notification to the dead letter queue.
     *
     * @param notificationId the notification ID
     * @param reason the reason for moving to DLQ
     * @return true if successful
     */
    boolean moveToDeadLetterQueue(UUID notificationId, String reason);

    /**
     * Gets notifications from the dead letter queue.
     *
     * @param limit the maximum number to retrieve
     * @return list of notification IDs
     */
    List<UUID> getDeadLetterQueue(int limit);

    /**
     * Retries notifications from the dead letter queue.
     *
     * @param notificationId the notification ID
     * @return true if successfully moved back to main queue
     */
    boolean retryFromDeadLetterQueue(UUID notificationId);

    /**
     * Gets queue statistics.
     *
     * @return map with statistics (size, avgWaitTime, oldestItemAge, etc.)
     */
    java.util.Map<String, Object> getQueueStatistics();

    /**
     * Pauses processing for the queue.
     *
     * @return true if successfully paused
     */
    boolean pauseQueue();

    /**
     * Resumes processing for the queue.
     *
     * @return true if successfully resumed
     */
    boolean resumeQueue();

    /**
     * Checks if the queue is paused.
     *
     * @return true if paused
     */
    boolean isQueuePaused();

    /**
     * Sets the maximum queue size.
     *
     * @param maxSize the maximum size
     * @return true if successful
     */
    boolean setMaxQueueSize(long maxSize);

    /**
     * Gets the maximum queue size.
     *
     * @return maximum queue size
     */
    long getMaxQueueSize();

    /**
     * Checks if a notification is in the queue.
     *
     * @param notificationId the notification ID
     * @return true if in queue
     */
    boolean isInQueue(UUID notificationId);

    /**
     * Gets the position of a notification in the queue.
     *
     * @param notificationId the notification ID
     * @return position (0-based), or -1 if not in queue
     */
    int getQueuePosition(UUID notificationId);

    /**
     * Gets estimated wait time for a notification in queue.
     *
     * @param notificationId the notification ID
     * @return estimated wait time in milliseconds
     */
    long getEstimatedWaitTime(UUID notificationId);

    /**
     * Bulk enqueues multiple notifications.
     *
     * @param notificationIds list of notification IDs
     * @param priority the priority level
     * @return number of successfully queued notifications
     */
    int bulkEnqueue(List<UUID> notificationIds, int priority);

    /**
     * Gets notifications queued before a specific time.
     *
     * @param timestamp the cutoff timestamp
     * @return list of notification IDs
     */
    List<UUID> getNotificationsQueuedBefore(LocalDateTime timestamp);

    /**
     * Removes stale notifications from queue (older than threshold).
     *
     * @param thresholdMinutes age threshold in minutes
     * @return number of notifications removed
     */
    int removeStaleNotifications(int thresholdMinutes);

    /**
     * Reorders queue based on updated priorities.
     *
     * @return true if successful
     */
    boolean reorderQueue();

    /**
     * Gets queue health status.
     *
     * @return health status (HEALTHY, DEGRADED, UNHEALTHY)
     */
    String getQueueHealth();

    /**
     * Archives processed notifications from queue.
     *
     * @param olderThanDays archive notifications older than this many days
     * @return number of notifications archived
     */
    long archiveProcessedNotifications(int olderThanDays);

    /**
     * Gets queue processing rate (notifications per second).
     *
     * @return processing rate
     */
    double getProcessingRate();

    /**
     * Sets the queue processing rate limit.
     *
     * @param maxPerSecond maximum notifications per second
     * @return true if successful
     */
    boolean setRateLimit(int maxPerSecond);

    /**
     * Gets the current rate limit.
     *
     * @return maximum notifications per second
     */
    int getRateLimit();

    /**
     * Gets average processing time per notification.
     *
     * @return average time in milliseconds
     */
    long getAverageProcessingTime();

    /**
     * Gets the number of failed processing attempts.
     *
     * @return count of failed attempts
     */
    long getFailedProcessingCount();

    /**
     * Resets queue statistics.
     *
     * @return true if successful
     */
    boolean resetStatistics();
}

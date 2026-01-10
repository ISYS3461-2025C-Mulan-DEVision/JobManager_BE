package com.devision.job_manager_notification.service.internal;

import com.devision.job_manager_notification.enums.NotificationType;
import com.devision.job_manager_notification.entity.Notification;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal service interface for notification caching operations.
 * Manages caching of notifications, preferences, and templates for performance optimization.
 */
public interface NotificationCacheService {

    /**
     * Caches a notification.
     *
     * @param notification the notification to cache
     * @param ttl time-to-live duration
     */
    void cacheNotification(Notification notification, Duration ttl);

    /**
     * Gets a cached notification.
     *
     * @param notificationId the notification ID
     * @return the cached notification, or null if not found
     */
    Notification getCachedNotification(UUID notificationId);

    /**
     * Removes a notification from cache.
     *
     * @param notificationId the notification ID
     */
    void evictNotification(UUID notificationId);

    /**
     * Caches user notification preferences.
     *
     * @param userId the user ID
     * @param preferences the preferences map
     * @param ttl time-to-live duration
     */
    void cacheUserPreferences(UUID userId, Map<String, Object> preferences, Duration ttl);

    /**
     * Gets cached user preferences.
     *
     * @param userId the user ID
     * @return the cached preferences, or null if not found
     */
    Map<String, Object> getCachedUserPreferences(UUID userId);

    /**
     * Evicts user preferences from cache.
     *
     * @param userId the user ID
     */
    void evictUserPreferences(UUID userId);

    /**
     * Caches a notification template.
     *
     * @param templateId the template ID
     * @param templateContent the template content
     * @param ttl time-to-live duration
     */
    void cacheTemplate(UUID templateId, String templateContent, Duration ttl);

    /**
     * Gets cached template content.
     *
     * @param templateId the template ID
     * @return the cached template, or null if not found
     */
    String getCachedTemplate(UUID templateId);

    /**
     * Evicts a template from cache.
     *
     * @param templateId the template ID
     */
    void evictTemplate(UUID templateId);

    /**
     * Caches notification count for a user.
     *
     * @param userId the user ID
     * @param count the notification count
     * @param ttl time-to-live duration
     */
    void cacheNotificationCount(UUID userId, long count, Duration ttl);

    /**
     * Gets cached notification count.
     *
     * @param userId the user ID
     * @return the cached count, or null if not found
     */
    Long getCachedNotificationCount(UUID userId);

    /**
     * Increments cached notification count.
     *
     * @param userId the user ID
     * @return the new count
     */
    long incrementNotificationCount(UUID userId);

    /**
     * Decrements cached notification count.
     *
     * @param userId the user ID
     * @return the new count
     */
    long decrementNotificationCount(UUID userId);

    /**
     * Caches unread notification count.
     *
     * @param userId the user ID
     * @param count the unread count
     * @param ttl time-to-live duration
     */
    void cacheUnreadCount(UUID userId, long count, Duration ttl);

    /**
     * Gets cached unread count.
     *
     * @param userId the user ID
     * @return the cached unread count, or null if not found
     */
    Long getCachedUnreadCount(UUID userId);

    /**
     * Caches recent notifications for a user.
     *
     * @param userId the user ID
     * @param notifications the list of recent notifications
     * @param ttl time-to-live duration
     */
    void cacheRecentNotifications(UUID userId, List<Notification> notifications, Duration ttl);

    /**
     * Gets cached recent notifications.
     *
     * @param userId the user ID
     * @return the cached notifications, or null if not found
     */
    List<Notification> getCachedRecentNotifications(UUID userId);

    /**
     * Caches delivery status.
     *
     * @param notificationId the notification ID
     * @param status the delivery status
     * @param ttl time-to-live duration
     */
    void cacheDeliveryStatus(UUID notificationId, String status, Duration ttl);

    /**
     * Gets cached delivery status.
     *
     * @param notificationId the notification ID
     * @return the cached status, or null if not found
     */
    String getCachedDeliveryStatus(UUID notificationId);

    /**
     * Caches analytics data.
     *
     * @param key the cache key
     * @param data the analytics data
     * @param ttl time-to-live duration
     */
    void cacheAnalytics(String key, Map<String, Object> data, Duration ttl);

    /**
     * Gets cached analytics data.
     *
     * @param key the cache key
     * @return the cached data, or null if not found
     */
    Map<String, Object> getCachedAnalytics(String key);

    /**
     * Caches rate limit counter.
     *
     * @param userId the user ID
     * @param type the notification type
     * @param count the current count
     * @param ttl time-to-live duration
     */
    void cacheRateLimitCounter(UUID userId, NotificationType type, int count, Duration ttl);

    /**
     * Gets cached rate limit counter.
     *
     * @param userId the user ID
     * @param type the notification type
     * @return the cached count, or null if not found
     */
    Integer getCachedRateLimitCounter(UUID userId, NotificationType type);

    /**
     * Increments rate limit counter.
     *
     * @param userId the user ID
     * @param type the notification type
     * @return the new count
     */
    int incrementRateLimitCounter(UUID userId, NotificationType type);

    /**
     * Clears all cache entries.
     */
    void clearAllCache();

    /**
     * Clears cache for a specific user.
     *
     * @param userId the user ID
     */
    void clearUserCache(UUID userId);

    /**
     * Clears cache for a specific notification type.
     *
     * @param type the notification type
     */
    void clearTypeCache(NotificationType type);

    /**
     * Gets cache statistics.
     *
     * @return map with cache statistics (hits, misses, size, etc.)
     */
    Map<String, Object> getCacheStatistics();

    /**
     * Warms up cache with frequently accessed data.
     */
    void warmUpCache();

    /**
     * Checks if a key exists in cache.
     *
     * @param key the cache key
     * @return true if exists
     */
    boolean exists(String key);

    /**
     * Gets remaining TTL for a cache entry.
     *
     * @param key the cache key
     * @return remaining TTL in seconds, or -1 if not found
     */
    long getRemainingTTL(String key);

    /**
     * Updates TTL for an existing cache entry.
     *
     * @param key the cache key
     * @param newTtl the new TTL duration
     * @return true if successful
     */
    boolean updateTTL(String key, Duration newTtl);

    /**
     * Gets cache hit rate.
     *
     * @return hit rate as percentage (0-100)
     */
    double getCacheHitRate();

    /**
     * Gets cache miss rate.
     *
     * @return miss rate as percentage (0-100)
     */
    double getCacheMissRate();

    /**
     * Gets total cache size in bytes.
     *
     * @return cache size in bytes
     */
    long getCacheSize();

    /**
     * Gets number of cached entries.
     *
     * @return number of entries
     */
    long getCacheEntryCount();

    /**
     * Evicts expired cache entries.
     *
     * @return number of entries evicted
     */
    long evictExpiredEntries();

    /**
     * Evicts least recently used entries to free space.
     *
     * @param targetSize the target size to reach
     * @return number of entries evicted
     */
    long evictLRU(long targetSize);

    /**
     * Caches a list of items with pattern-based key.
     *
     * @param pattern the key pattern
     * @param items the items to cache
     * @param ttl time-to-live duration
     */
    void cacheList(String pattern, List<?> items, Duration ttl);

    /**
     * Gets cached list items.
     *
     * @param pattern the key pattern
     * @return the cached list, or null if not found
     */
    List<?> getCachedList(String pattern);

    /**
     * Bulk evicts cache entries by pattern.
     *
     * @param pattern the key pattern (supports wildcards)
     * @return number of entries evicted
     */
    long bulkEvict(String pattern);

    /**
     * Exports cache contents to backup.
     *
     * @return serialized cache data
     */
    String exportCache();

    /**
     * Imports cache contents from backup.
     *
     * @param cacheData the serialized cache data
     * @return true if successful
     */
    boolean importCache(String cacheData);

    /**
     * Checks cache health status.
     *
     * @return health status (HEALTHY, DEGRADED, UNHEALTHY)
     */
    String getCacheHealth();
}

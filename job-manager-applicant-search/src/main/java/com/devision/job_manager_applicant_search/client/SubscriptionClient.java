package com.devision.job_manager_applicant_search.client;

import com.devision.job_manager_applicant_search.event.SubscriptionUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Client for checking subscription/premium status.
 * Uses Redis cache populated by Kafka events from the Subscription Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionClient {

    private static final String CACHE_KEY_PREFIX = "subscription:premium:";
    private static final long CACHE_TTL_MINUTES = 60; // 1 hour TTL

    private final RedisTemplate<String, Boolean> redisTemplate;

    /**
     * Checks if a company has premium status.
     * Returns cached value from Redis, populated by Kafka events.
     * 
     * @param companyId the company UUID
     * @return true if the company is premium, false otherwise
     */
    public boolean isPremium(UUID companyId) {
        String cacheKey = CACHE_KEY_PREFIX + companyId.toString();
        Boolean isPremium = redisTemplate.opsForValue().get(cacheKey);
        
        if (isPremium == null) {
            // If not in cache, treat as not premium (fail-safe)
            log.debug("Premium status not found in cache for company: {}", companyId);
            return false;
        }
        
        return isPremium;
    }

    /**
     * Updates the cached premium status for a company.
     * Called when a subscription update event is received from Kafka.
     * 
     * @param event the subscription updated event
     */
    public void updatePremiumStatus(SubscriptionUpdatedEvent event) {
        String cacheKey = CACHE_KEY_PREFIX + event.getCompanyId().toString();
        
        boolean isPremium = event.isPremium();
        
        // Also check if subscription has expired based on endAt
        if (isPremium && event.getEndAt() != null) {
            isPremium = event.getEndAt().isAfter(OffsetDateTime.now());
        }
        
        redisTemplate.opsForValue().set(cacheKey, isPremium, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.info("Updated premium status cache for company {}: {}", event.getCompanyId(), isPremium);
    }

    /**
     * Manually sets the premium status for a company.
     * Useful for initialization or manual overrides.
     * 
     * @param companyId the company UUID
     * @param isPremium the premium status
     */
    public void setPremiumStatus(UUID companyId, boolean isPremium) {
        String cacheKey = CACHE_KEY_PREFIX + companyId.toString();
        redisTemplate.opsForValue().set(cacheKey, isPremium, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("Set premium status cache for company {}: {}", companyId, isPremium);
    }

    /**
     * Removes the cached premium status for a company.
     * 
     * @param companyId the company UUID
     */
    public void evictPremiumStatus(UUID companyId) {
        String cacheKey = CACHE_KEY_PREFIX + companyId.toString();
        redisTemplate.delete(cacheKey);
        log.debug("Evicted premium status cache for company: {}", companyId);
    }
}

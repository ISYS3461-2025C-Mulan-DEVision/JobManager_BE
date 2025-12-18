package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.dto.internal.PendingSsoRegistration;
import com.devision.job_manager_auth.service.internal.SsoRegistrationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SsoRegistrationCacheServiceImpl implements SsoRegistrationCacheService {
    private static final String KEY_PREFIX = "pending-sso:";
    private static final long TTL_MINUTES = 10;

    private final RedisTemplate<String, Object> redisTemplate;

    public SsoRegistrationCacheServiceImpl(@Qualifier("redisObjectTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String storePendingRegistration(PendingSsoRegistration registration) {
        String token = UUID.randomUUID().toString();
        String key = KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, registration, TTL_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Optional<PendingSsoRegistration> retrieveAndDeletePendingRegistration(String token) {
        String key = KEY_PREFIX + token;
        PendingSsoRegistration registration = (PendingSsoRegistration) redisTemplate.opsForValue().get(key);
        if (registration != null) {
            redisTemplate.delete(key);
            return Optional.of(registration);
        }
        return Optional.empty();
    }
}

package com.funding.backend.security.jwt;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "refreshToken:";

    public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
        String key = PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationMillis));
    }

    public String getRefreshToken(Long userId) {
        String key = PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(Long userId) {
        String key = PREFIX + userId;
        redisTemplate.delete(key);
    }

    public boolean isValid(Long userId, String refreshToken) {
        String stored = getRefreshToken(userId);
        return stored != null && stored.equals(refreshToken);
    }
}
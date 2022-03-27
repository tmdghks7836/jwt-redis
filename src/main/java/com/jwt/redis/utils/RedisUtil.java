package com.jwt.redis.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<Object, Object> redisTemplate;

    public <T extends Object> T getData(String key) {
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();
        return (T) valueOperations.get(key);
    }

    public void setData(String key, Object value) {
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataContainsExpireDate(String key, Object value, long duration) {
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

}
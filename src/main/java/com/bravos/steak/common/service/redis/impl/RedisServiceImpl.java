package com.bravos.steak.common.service.redis.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public void save(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public void zsave(String key, Object value, double score) {
        redisTemplate.opsForZSet().add("leaderboard:game", value, score);
    }

    @Override
    public double zget(String key, Object value) {
        Double score = redisTemplate.opsForZSet().score(key, value);
        return score != null ? score : 0.0;
    }

    @Override
    public double zincrement(String key, Object value, double delta) {
        if (delta == 0) {
            return zget(key, value);
        }
        Double score = redisTemplate.opsForZSet().incrementScore(key, value, delta);
        if (score != null) return score;
        return 0.0;
    }

    @Override
    public double zdecrement(String key, Object value, double delta) {
        if (delta == 0) {
            return zget(key, value);
        }
        Double score = redisTemplate.opsForZSet().incrementScore(key, value, -delta);
        if (score != null) return score;
        return 0.0;
    }

    @Override
    public Double decrementAndDeleteIfZero(String key, Object value, double delta) {
        String luaScript = """
                    local newScore = redis.call('ZINCRBY', KEYS[1], ARGV[1], ARGV[2])
                    if tonumber(newScore) <= 0 then
                        redis.call('ZREM', KEYS[1], ARGV[2])
                    end
                    return newScore
                """;
        DefaultRedisScript<Double> script = new DefaultRedisScript<>(luaScript);

        String member;
        try {
            member = value instanceof String ? (String) value : objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing value: " + e.getMessage(), e);
        }
        return redisTemplate.execute(
                script,
                Collections.singletonList(key),
                delta,
                member
        );
    }

    @Override
    public boolean saveIfAbsent(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public boolean saveIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit));
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        try {
            return objectMapper.convertValue(value, clazz);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <E, C extends Collection<E>> C get(String key, CollectionLikeType type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        if (!(value instanceof Collection)) {
            throw new RuntimeException("Value is not a collection: " + value.getClass().getName());
        }
        try {
            return objectMapper.convertValue(value, type);
        } catch (IllegalArgumentException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> List<T> multiGet(Collection<String> key, Class<T> clazz) {
        List<Object> values = redisTemplate.opsForValue().multiGet(key);
        if (values == null || values.isEmpty()) return List.of();
        try {
            return objectMapper.convertValue(values,
                    objectMapper.getTypeFactory().constructCollectionLikeType(List.class, clazz));
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T getAndSet(String key, Object value, Class<T> clazz) {
        try {
            return objectMapper.convertValue(redisTemplate.opsForValue().getAndSet(key, value), clazz);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T getAndDelete(String key, Class<T> clazz) {
        try {
            return objectMapper.convertValue(redisTemplate.opsForValue().getAndDelete(key), clazz);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    private <T> T getWithLockInternal(
            String key,
            String lockKey,
            Supplier<T> getFunction,
            Supplier<T> fallbackFunction,
            long lockTimeout,
            TimeUnit lockTimeUnit,
            long retryTime,
            long retryWait,
            long keyTimeout,
            TimeUnit keyTimeUnit
    ) {
        T value = getFunction.get();
        if (value != null) return value;

        boolean isLockAcquired = this.saveIfAbsent(lockKey, 1, lockTimeout, lockTimeUnit);

        if (!isLockAcquired) {
            try {
                for (int i = 0; i < retryTime; i++) {
                    Thread.sleep(Duration.ofMillis(retryWait));
                    value = getFunction.get();
                    if (value != null) {
                        return value;
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error when waiting get cache: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }

        try {
            value = fallbackFunction.get();
            this.save(key, value, keyTimeout, keyTimeUnit);
            return value;
        } finally {
            if (isLockAcquired) {
                this.delete(lockKey);
            }
        }
    }

    @Override
    public <T> T getWithLock(RedisCacheEntry<T> cacheEntry, Class<T> clazz) {
        final String key = cacheEntry.getKey();
        final String lockKey = "get-lock:" + key;
        return getWithLockInternal(
                key,
                lockKey,
                () -> this.get(key, clazz),
                cacheEntry.getFallBackFunction(),
                cacheEntry.getLockTimeout(),
                cacheEntry.getLockTimeUnit(),
                cacheEntry.getRetryTime(),
                cacheEntry.getRetryWait(),
                cacheEntry.getKeyTimeout(),
                cacheEntry.getKeyTimeUnit()
        );
    }

    @Override
    public <E, C extends Collection<E>> C getWithLock(RedisCacheEntry<C> cacheEntry, CollectionLikeType type) {
        final String key = cacheEntry.getKey();
        final String lockKey = "get-lock:" + key;
        return getWithLockInternal(
                key,
                lockKey,
                () -> this.get(key, type),
                cacheEntry.getFallBackFunction(),
                cacheEntry.getLockTimeout(),
                cacheEntry.getLockTimeUnit(),
                cacheEntry.getRetryTime(),
                cacheEntry.getRetryWait(),
                cacheEntry.getKeyTimeout(),
                cacheEntry.getKeyTimeUnit()
        );
    }


    @Override
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public Double increment(String key, double delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (IllegalArgumentException | ClassCastException e) {
            log.error("Error when converting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when converting data");
        } catch (Exception e) {
            log.error("Error when getting data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error("Error when revoking resource: {}", e.getMessage(), e);
            throw new RuntimeException("Error when revoking resource");
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error when deleting resource: {}", e.getMessage(), e);
            throw new RuntimeException("Error when deleting resource");
        }
    }

    @Override
    public Cursor<String> scan(ScanOptions options) {
        try {
            return redisTemplate.scan(options);
        } catch (Exception e) {
            log.error("Error when scanning keys: {}", e.getMessage(), e);
            throw new RuntimeException("Error when scanning keys");
        }
    }

    @Override
    public Cursor<ZSetOperations.TypedTuple<Object>> zscan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }

    @Override
    public Set<Object> zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    @Override
    public long zcount(String key) {
        Long count = redisTemplate.opsForZSet().size(key);
        return count != null ? count : 0L;
    }

    @Override
    public void clearAll() {
        try {
            RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
            if (redisConnectionFactory != null && !redisConnectionFactory.getConnection().isClosed()) {
                redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
                return;
            }
            log.error("Redis connection is closed");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

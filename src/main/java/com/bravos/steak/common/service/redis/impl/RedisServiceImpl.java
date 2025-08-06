package com.bravos.steak.common.service.redis.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public <T> Collection<T> get(String key, CollectionLikeType type, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return List.of();
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

    @Override
    public <T> T getWithLock(RedisCacheEntry<T> cacheEntry, Class<T> clazz) {
        final String key = cacheEntry.getKey();
        final String lockKey = "get-lock:" + key;
        T value = this.get(key, clazz);
        if (value != null) return value;

        boolean isLockAcquired = this.saveIfAbsent(lockKey, 1,
                cacheEntry.getLockTimeout(), cacheEntry.getLockTimeUnit());

        if (!isLockAcquired) {
            try {
                for (int i = 0; i < cacheEntry.getRetryTime(); i++) {
                    Thread.sleep(Duration.ofMillis(cacheEntry.getRetryWait()));
                    value = this.get(key, clazz);
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
            value = cacheEntry.getFallBackFunction().get();
            this.save(key, value, cacheEntry.getKeyTimeout(), cacheEntry.getKeyTimeUnit());
            return value;
        } finally {
            if (isLockAcquired) {
                this.delete(lockKey);
            }
        }
    }

    @Override
    public <T> Collection<T> getWithLock(RedisCacheEntry<Collection<T>> cacheEntry, CollectionLikeType type, Class<T> clazz) {
        final String key = cacheEntry.getKey();
        final String lockKey = "get-lock:" + key;
        Collection<T> value = this.get(key, type, clazz);
        if (value != null) return value;

        boolean isLockAcquired = this.saveIfAbsent(lockKey, 1,
                cacheEntry.getLockTimeout(), cacheEntry.getLockTimeUnit());

        if (!isLockAcquired) {
            try {
                for (int i = 0; i < cacheEntry.getRetryTime(); i++) {
                    Thread.sleep(Duration.ofMillis(cacheEntry.getRetryWait()));
                    value = this.get(key, type, clazz);
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
            value = cacheEntry.getFallBackFunction().get();
            this.save(key, value, cacheEntry.getKeyTimeout(), cacheEntry.getKeyTimeUnit());
            return value;
        } finally {
            if (isLockAcquired) {
                this.delete(lockKey);
            }
        }
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
    public Cursor<byte[]> scan(KeyScanOptions options) {
        try {
            RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
            if (redisConnectionFactory != null && !redisConnectionFactory.getConnection().isClosed()) {
                RedisConnection connection = redisConnectionFactory.getConnection();
                if (!connection.isClosed()) {
                    return connection.scan(options);
                }
            }
            throw new RuntimeException("Redis connection is closed");
        } catch (Exception e) {
            log.error("Error when scanning keys: {}", e.getMessage(), e);
            throw new RuntimeException("Error when scanning keys");
        }
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

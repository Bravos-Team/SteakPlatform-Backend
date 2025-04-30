package com.bravos.steak.common.service.redis.impl;

import com.bravos.steak.common.service.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key,value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public void save(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key,value,timeout,timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public boolean saveIfAbsent(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public boolean saveIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key,value,timeout,timeUnit));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) return null;
        try {
            return objectMapper.convertValue(value,clazz);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> List<T> multiGet(Collection<String> key, Class<T> clazz) {
        List<Object> values = redisTemplate.opsForValue().multiGet(key);
        if(values == null || values.isEmpty()) return List.of();
        try {
            return objectMapper.convertValue(values,objectMapper.getTypeFactory().constructCollectionLikeType(List.class,clazz));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T getAndSet(String key, Object value, Class<T> clazz) {
        try {
            return objectMapper.convertValue(redisTemplate.opsForValue().getAndSet(key,value),clazz);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public <T> T getAndDelete(String key, Class<T> clazz) {
        try {
            return objectMapper.convertValue(redisTemplate.opsForValue().getAndDelete(key),clazz);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key,delta);
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key,delta);
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public Double increment(String key, double delta) {
        try {
            return redisTemplate.opsForValue().increment(key,delta);
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when convert data");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when getting data");
        }
    }

    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when deleting resource");
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

package com.bravos.steak.common.service.redis;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void save(String key, Object value);

    void save(String key, Object value, long timeout, TimeUnit timeUnit);

    boolean saveIfAbsent(String key, Object value);

    boolean saveIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit);

    boolean hasKey(String key);

    <T> T get(String key, Class<T> clazz);

    <T> Collection<T> get(String key, CollectionLikeType type, Class<T> clazz);

    <T> List<T> multiGet(Collection<String> key, Class<T> clazz);

    <T> T getAndSet(String key, Object value, Class<T> clazz);

    <T> T getAndDelete(String key, Class<T> clazz);

    <T> T getWithLock(RedisCacheEntry<T> cacheEntry, Class<T> clazz);

    <T> Collection<T> getWithLock(RedisCacheEntry<Collection<T>> cacheEntry, CollectionLikeType type, Class<T> clazz);

    Long increment(String key, long delta);

    Long decrement(String key, long delta);

    Double increment(String key, double delta);

    void expire(String key, long timeout, TimeUnit timeUnit);

    void delete(String key);

    Cursor<byte[]> scan(KeyScanOptions options);

    void clearAll();

}

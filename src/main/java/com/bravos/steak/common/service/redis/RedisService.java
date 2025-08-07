package com.bravos.steak.common.service.redis;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void save(String key, Object value);

    void save(String key, Object value, long timeout, TimeUnit timeUnit);

    void zsave(String key, Object value, double score);

    double zget(String key, Object value);

    double zincrement(String key, Object value, double delta);

    double zdecrement(String key, Object value, double delta);

    Double decrementAndDeleteIfZero(String key, Object value, double delta);

    boolean saveIfAbsent(String key, Object value);

    boolean saveIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit);

    boolean hasKey(String key);

    <T> T get(String key, Class<T> clazz);

    <E, C extends Collection<E>> C get(String key, CollectionLikeType type);

    <T> List<T> multiGet(Collection<String> key, Class<T> clazz);

    <T> T getAndSet(String key, Object value, Class<T> clazz);

    <T> T getAndDelete(String key, Class<T> clazz);

    <T> T getWithLock(RedisCacheEntry<T> cacheEntry, Class<T> clazz);

    <E, C extends Collection<E>> C getWithLock(RedisCacheEntry<C> cacheEntry, CollectionLikeType type);

    Long increment(String key, long delta);

    Long decrement(String key, long delta);

    Double increment(String key, double delta);

    void expire(String key, long timeout, TimeUnit timeUnit);

    void delete(String key);

    Cursor<String> scan(ScanOptions options);

    Cursor<ZSetOperations.TypedTuple<Object>> zscan(String key, ScanOptions options);

    Set<Object> zrange(String key, long start, long end);

    long zcount(String key);

    void clearAll();

}

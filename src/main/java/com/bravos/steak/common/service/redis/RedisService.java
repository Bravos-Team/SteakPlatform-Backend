package com.bravos.steak.common.service.redis;

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

    <T> List<T> multiGet(Collection<String> key, Class<T> clazz);

    <T> T getAndSet(String key, Object value, Class<T> clazz);

    <T> T getAndDelete(String key, Class<T> clazz);

    <T extends Number> T increment(String key, long delta, Class<T> type);

    <T extends Number> T decrement(String key, long delta, Class<T> type);

    <T extends Number> T increment(String key, double delta, Class<T> type);

    void delete(String key);

    void clearAll();

}

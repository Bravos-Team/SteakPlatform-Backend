package com.bravos.steak.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@Builder
public class RedisCacheEntry<T> {

    String key;

    Supplier<T> fallBackFunction;

    @Builder.Default
    long keyTimeout = 60;

    @Builder.Default
    TimeUnit keyTimeUnit = TimeUnit.SECONDS;

    @Builder.Default
    long lockTimeout = 100;

    @Builder.Default
    TimeUnit lockTimeUnit = TimeUnit.MILLISECONDS;

    @Builder.Default
    int retryTime = 3;

    @Builder.Default
    int retryWait = 50;

}

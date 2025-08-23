package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.model.response.StatisticResponse;
import com.bravos.steak.administration.repo.AdminStatisticRepository;
import com.bravos.steak.administration.service.AdminStatisticService;
import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminStatisticServiceImpl implements AdminStatisticService {

    private final AdminStatisticRepository adminStatisticRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public AdminStatisticServiceImpl(AdminStatisticRepository adminStatisticRepository,
                                     RedisService redisService, ObjectMapper objectMapper) {
        this.adminStatisticRepository = adminStatisticRepository;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<StatisticResponse> getRevenueStatisticByYear() {
        RedisCacheEntry<List<StatisticResponse>> cacheEntry = RedisCacheEntry.<List<StatisticResponse>>builder()
                .key("admin:statistics:revenue:yearly")
                .fallBackFunction(adminStatisticRepository::getRevenueStatisticByYear)
                .keyTimeout(10)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(6)
                .lockTimeUnit(TimeUnit.SECONDS)
                .retryTime(3)
                .build();
        return redisService.getWithLock(cacheEntry, objectMapper.getTypeFactory().constructCollectionType(List.class, StatisticResponse.class));
    }

    @Override
    public List<StatisticResponse> getRevenueStatisticByMonth(int year) {
        RedisCacheEntry<List<StatisticResponse>> cacheEntry = RedisCacheEntry.<List<StatisticResponse>>builder()
                .key("admin:statistics:revenue:monthly:" + year)
                .fallBackFunction(() -> adminStatisticRepository.getRevenueStatisticByMonth(year))
                .keyTimeout(10)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(6)
                .lockTimeUnit(TimeUnit.SECONDS)
                .retryTime(3)
                .build();
        return redisService.getWithLock(cacheEntry, objectMapper.getTypeFactory().constructCollectionType(List.class, StatisticResponse.class));
    }

    @Override
    public List<StatisticResponse> getRevenueStatisticByDay(int month, int year) {
        RedisCacheEntry<List<StatisticResponse>> cacheEntry = RedisCacheEntry.<List<StatisticResponse>>builder()
                .key("admin:statistics:revenue:daily:" + year + ":" + month)
                .fallBackFunction(() -> adminStatisticRepository.getRevenueStatisticByDay(month, year))
                .keyTimeout(2)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(6)
                .lockTimeUnit(TimeUnit.SECONDS)
                .retryTime(3)
                .build();
        return redisService.getWithLock(cacheEntry, objectMapper.getTypeFactory().constructCollectionType(List.class, StatisticResponse.class));
    }
}

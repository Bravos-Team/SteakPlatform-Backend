package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.CustomPage;
import com.bravos.steak.common.model.CustomPageInfo;
import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.dev.model.GameThumbnail;
import com.bravos.steak.dev.model.response.GameStatisticItem;
import com.bravos.steak.dev.service.GameStatisticService;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.OrderDetailsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GameStatisticServiceImpl implements GameStatisticService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final SessionService sessionService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public GameStatisticServiceImpl(OrderDetailsRepository orderDetailsRepository,
                                    GameDetailsRepository gameDetailsRepository,
                                    SessionService sessionService,
                                    RedisService redisService,
                                    ObjectMapper objectMapper) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.gameDetailsRepository = gameDetailsRepository;
        this.sessionService = sessionService;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    @Override
    public CustomPage<GameStatisticItem> getGameStatisticsRevenue(Integer month, Integer year, int page, int pageSize) {
        String key = "game_statistics_revenue_" + month + "_" + year + "_" + page + "_" + pageSize + "_" + getCurrentPublisherId();
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getGameStatisticsRevenueFromDb(month, year, page, pageSize))
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .retryWait(500)
                .retryTime(3)
                .lockTimeout(1500)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    public CustomPage<GameStatisticItem> getGameStatisticsRevenueFromDb(Integer month, Integer year, int page, int pageSize) {
        Page<GameStatisticItem> result;
        Long publisherId = getCurrentPublisherId();
        if(month == null && year == null) {
            result = orderDetailsRepository.getGameStatisticsRevenue(publisherId, PageRequest.of(page, pageSize));
        } else {
            LocalDateTime from;
            LocalDateTime to;
            if(month == null) {
                from = LocalDateTime.of(year, 1, 1, 0, 0);
                to = from.plusYears(1);
            } else if(year == null) {
                from = LocalDateTime.of(LocalDateTime.now().getYear(), month, 1, 0, 0);
                to = from.plusMonths(1);
            } else {
                from = LocalDateTime.of(year, month, 1, 0, 0);
                to = from.plusMonths(1);
            }
            Long fromMillis = DateTimeHelper.from(from);
            Long toMillis = DateTimeHelper.from(to);
            result = orderDetailsRepository.getGameStatisticsRevenue(publisherId, fromMillis, toMillis, PageRequest.of(page, pageSize));
        }

        if(result.isEmpty()) return new CustomPage<>(result);

        List<Long> gameIds = result.stream().map(GameStatisticItem::getGameId).toList();
        List<GameThumbnail> thumbnails = gameDetailsRepository.findThumbnailsByIdIn(gameIds);
        Map<Long, GameStatisticItem> resultMap = result.stream().collect(
                Collectors.toMap(GameStatisticItem::getGameId, item -> item)
        );

        for(GameThumbnail thumbnail : thumbnails) {
            GameStatisticItem item = resultMap.get(thumbnail.getId());
            item.setThumbnail(thumbnail.getThumbnail());
        }

        List<GameStatisticItem> content = resultMap.values().stream().toList();

        return new CustomPage<>(content, CustomPageInfo.builder()
                .number(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build());
    }

    private Long getCurrentPublisherId() {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        return Long.parseLong(claims.getOtherClaims().get("publisherId").toString());
    }

}

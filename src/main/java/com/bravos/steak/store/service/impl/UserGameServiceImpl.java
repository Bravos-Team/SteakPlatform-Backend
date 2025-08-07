package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.PlayingCountRecord;
import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.repo.PlayingCountRecordRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.store.service.UserGameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserGameServiceImpl implements UserGameService {

    private final UserGameRepository userGameRepository;
    private final RedisService redisService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final PlayingCountRecordRepository playingCountRecordRepository;

    private static final String GAME_LEADERBOARD_KEY = "leaderboard:game";
    private final ObjectMapper objectMapper;

    public UserGameServiceImpl(UserGameRepository userGameRepository, RedisService redisService,
                               SnowflakeGenerator snowflakeGenerator, PlayingCountRecordRepository playingCountRecordRepository,
                               ObjectMapper objectMapper) {
        this.userGameRepository = userGameRepository;
        this.redisService = redisService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.playingCountRecordRepository = playingCountRecordRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void updateUserGame(long userId, long gameId, long playTime, long currentTime) {
        UserGame userGame = userGameRepository.findByGameIdAndUserId(gameId, userId);
        if(userGame != null && (userGame.getPlayRecentDate() == null || currentTime > userGame.getPlayRecentDate() )) {
            userGame.setPlayRecentDate(currentTime);
            userGame.setPlaySeconds(userGame.getPlaySeconds() + playTime / 1000);
            try {
                userGameRepository.save(userGame);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update user game", e);
            }
        }
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    @Override
    public void savePlayingCountJob() {
        String lockKey = "lock:save:playing:count";
        try {
            if(redisService.saveIfAbsent(lockKey, System.getProperty("MACHINE_ID"))) {
                savePlayingCount();
            }
        } catch (Exception e) {
            log.error("Failed to save playing count", e);
            throw new RuntimeException("Failed to save playing count", e);
        } finally {
            String value = redisService.get(lockKey, String.class);
            if(value != null && value.equals(System.getProperty("MACHINE_ID"))) {
                redisService.delete(lockKey);
            }
        }
    }

    private void savePlayingCount() {
        ScanOptions options = KeyScanOptions.scanOptions()
                .count(200)
                .build();

        List<PlayingCountRecord> records = new ArrayList<>(200);

        try(var cursor = redisService.zscan(GAME_LEADERBOARD_KEY, options)) {
            while (cursor.hasNext()) {
                var current = cursor.next();
                Long gameId = Long.parseLong(String.valueOf(current.getValue()));
                long score = Objects.requireNonNull(current.getScore()).longValue();
                if(score > 0) {
                    PlayingCountRecord record = PlayingCountRecord.builder()
                            .id(snowflakeGenerator.generateId())
                            .game(Game.builder().id(gameId).build())
                            .count(score)
                            .build();
                    records.add(record);
                }
                if(records.size() == 100) {
                    playingCountRecordRepository.saveAllAndFlush(records);
                    records.clear();
                }
            }
            if(!records.isEmpty()) {
                playingCountRecordRepository.saveAllAndFlush(records);
            }
        }
    }

    @Override
    public Set<Long> getTopPlayedGames(long start, long end) {
        if(start > end) {
            throw new IllegalArgumentException("Start index cannot be greater than end index");
        }
        String key = "game:leaderboard:" + start + ":" + end;
        RedisCacheEntry<Set<Long>> cacheEntry = RedisCacheEntry.<Set<Long>>builder()
                .key(key)
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(5)
                .lockTimeUnit(TimeUnit.SECONDS)
                .fallBackFunction(() -> getTopPlayedGamesFromRedis(start, end))
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(List.class, Long.class);
        return redisService.getWithLock(cacheEntry, type);
    }

    @Override
    public long countTotalPlayedGames() {
        String key = "game:leaderboard:count";
        RedisCacheEntry<Long> cacheEntry = RedisCacheEntry.<Long>builder()
                .key(key)
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(1)
                .lockTimeUnit(TimeUnit.SECONDS)
                .fallBackFunction(this::countTotalPlayedGamesFromRedis)
                .build();
        return redisService.getWithLock(cacheEntry, Long.class);
    }

    private long countTotalPlayedGamesFromRedis() {
        return redisService.zcount(GAME_LEADERBOARD_KEY);
    }

    private Set<Long> getTopPlayedGamesFromRedis(long start, long end) {
        return redisService.zrange(GAME_LEADERBOARD_KEY, start, end)
                .stream()
                .map(value -> Long.parseLong(String.valueOf(value)))
                .collect(Collectors.toSet());
    }

    @Override
    public void increaseCurrentPlayingGame(long gameId) {
        redisService.zincrement(GAME_LEADERBOARD_KEY, gameId, 1.0);
    }

    @Override
    public void decreaseCurrentPlayingGame(long gameId) {
        redisService.decrementAndDeleteIfZero(GAME_LEADERBOARD_KEY,gameId, 1.0);
    }

    @Override
    public long getCurrentPlayingGameCount(long gameId) {
        return Double.valueOf(redisService.zget(GAME_LEADERBOARD_KEY,gameId)).longValue();
    }


}

package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.PlayingCountRecord;
import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.repo.PlayingCountRecordRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.store.service.UserGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserGameServiceImpl implements UserGameService {

    private final UserGameRepository userGameRepository;
    private final RedisService redisService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final PlayingCountRecordRepository playingCountRecordRepository;

    public UserGameServiceImpl(UserGameRepository userGameRepository, RedisService redisService,
                               SnowflakeGenerator snowflakeGenerator, PlayingCountRecordRepository playingCountRecordRepository) {
        this.userGameRepository = userGameRepository;
        this.redisService = redisService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.playingCountRecordRepository = playingCountRecordRepository;
    }

    @Override
    public void updateUserGame(long userId, long gameId, long playTime, long currentTime) {
        UserGame userGame = userGameRepository.findByGameIdAndUserId(gameId, userId);
        if(userGame != null && currentTime > userGame.getPlayRecentDate()) {
            userGame.setPlayRecentDate(currentTime);
            userGame.setPlaySeconds(userGame.getPlaySeconds() + playTime / 1000);
            try {
                userGameRepository.save(userGame);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update user game", e);
            }
        }
    }

    @Scheduled(cron = "0 0 */1 * * *")
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
        String keyPattern  = "current:playing:game:*";
        ScanOptions options = KeyScanOptions.scanOptions()
                .match(keyPattern)
                .count(200)
                .build();
        List<PlayingCountRecord> records = new ArrayList<>(200);
        try(Cursor<byte[]> cursor = redisService.scan((KeyScanOptions) options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Long gameIdFromKey = Long.parseLong(key.substring(key.lastIndexOf(':') + 1));
                Long value = redisService.get(key, Long.class);
                if(value != null && value > 0) {
                    PlayingCountRecord record = PlayingCountRecord.builder()
                            .id(snowflakeGenerator.generateId())
                            .game(Game.builder().id(gameIdFromKey).build())
                            .count(value)
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
    public void increaseCurrentPlayingGame(long gameId) {
        String key = "current:playing:game:" + gameId;
        if(redisService.hasKey(key)) {
            redisService.increment(key, 1L);
            return;
        }
        redisService.save(key, 1L);
    }

    @Override
    public void decreaseCurrentPlayingGame(long gameId) {
        String key = "current:playing:game:" + gameId;
        redisService.decrement(key,1L);
    }

    @Override
    public long getCurrentPlayingGameCount(long gameId) {
        String key = "current:playing:game:" + gameId;
        Long count = redisService.get(key, Long.class);
        return count != null ? count : 0L;
    }



}

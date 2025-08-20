package com.bravos.steak.store.cronjob;

import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Top50MonthlyTrendingRecord;
import com.bravos.steak.store.entity.TrendingRecordId;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.PlayingCountRecordRepository;
import com.bravos.steak.store.repo.TopMonthlyGameRepository;
import com.bravos.steak.store.repo.injection.TrendingStatistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TrendingTrackingJob {

    private final GameRepository gameRepository;
    private final RedisService redisService;
    private final PlayingCountRecordRepository playingCountRecordRepository;
    private final TopMonthlyGameRepository topMonthlyGameRepository;

    public TrendingTrackingJob(GameRepository gameRepository,
                               RedisService redisService,
                               PlayingCountRecordRepository playingCountRecordRepository, TopMonthlyGameRepository topMonthlyGameRepository) {
        this.gameRepository = gameRepository;
        this.redisService = redisService;
        this.playingCountRecordRepository = playingCountRecordRepository;
        this.topMonthlyGameRepository = topMonthlyGameRepository;
    }

    @Scheduled(cron = "0 0 1 * * Mon")
    public void updateWeeklyTrending() {
        List<TrendingStatistic> trendingStatistics = gameRepository.getWeeklyTrendingStatistics();
        redisService.save("weeklyTrending", trendingStatistics);
        log.info("Weekly trending records updated");
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void updateMonthlyTrending() {
        List<TrendingStatistic> trendingStatistics = gameRepository.getMonthlyTrendingStatistics();
        redisService.save("monthlyTrending", trendingStatistics);
        int currentMonth = LocalDate.now().getMonth().getValue();
        int currentYear = LocalDate.now().getYear();
        List<Top50MonthlyTrendingRecord> results = new ArrayList<>(50);
        int rank = 1;
        for (TrendingStatistic statistic : trendingStatistics) {
            Top50MonthlyTrendingRecord record = Top50MonthlyTrendingRecord.builder()
                    .id(TrendingRecordId.builder()
                            .month(currentMonth)
                            .year(currentYear)
                            .rank(rank++)
                            .build())
                    .game(Game.builder().id(statistic.getGameId()).build())
                    .peakConcurrent(statistic.getPeakConcurrent())
                    .avgConcurrent(statistic.getAvgConcurrent())
                    .growthRate(statistic.getGrowthRate())
                    .trendingScore(statistic.getTrendingScore())
                    .build();
            results.add(record);
        }
        topMonthlyGameRepository.saveAllAndFlush(results);
        log.info("Monthly trending records updated for {}/{}", currentMonth, currentYear);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void updateDailyTrending() {
        List<TrendingStatistic> trendingStatistics = gameRepository.getDailyTrendingStatistics();
        redisService.save("dailyTrending", trendingStatistics);
        log.info("Daily trending records updated");
    }

}

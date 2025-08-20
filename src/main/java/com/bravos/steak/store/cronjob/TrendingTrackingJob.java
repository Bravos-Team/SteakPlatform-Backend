package com.bravos.steak.store.cronjob;

import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Top50MonthlyTrendingRecord;
import com.bravos.steak.store.entity.TrendingRecordId;
import com.bravos.steak.store.repo.TopMonthlyGameRepository;
import com.bravos.steak.store.repo.TrendingStatisticRepository;
import com.bravos.steak.store.repo.injection.TrendingStatistic;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TrendingTrackingJob {

    private final RedisService redisService;
    private final TopMonthlyGameRepository topMonthlyGameRepository;
    private final TrendingStatisticRepository trendingStatisticRepository;

    public TrendingTrackingJob(RedisService redisService,
                               TopMonthlyGameRepository topMonthlyGameRepository,
                               TrendingStatisticRepository trendingStatisticRepository) {
        this.redisService = redisService;
        this.topMonthlyGameRepository = topMonthlyGameRepository;
        this.trendingStatisticRepository = trendingStatisticRepository;
    }

    @Scheduled(cron = "0 0 0 * * Mon", zone = "GMT+7")
    public void updateWeeklyTrending() {
        log.info("Updating weekly trending records...");
        long startTime = System.currentTimeMillis();
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getWeeklyTrendingStatistics();
        redisService.save("weeklyTrending", trendingStatistics);
        long endTime = System.currentTimeMillis();
        log.info("Weekly trending records updated in {} ms", endTime - startTime);
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "GMT+7")
    @Transactional
    public void updateMonthlyTrending() {
        log.info("Updating monthly trending records...");
        long startTime = System.currentTimeMillis();
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getMonthlyTrendingStatistics();
        redisService.save("monthlyTrending", trendingStatistics);
        int currentMonth = LocalDate.now().getMonth().getValue();
        int currentYear = LocalDate.now().getYear();

        if (!topMonthlyGameRepository.existsByTime(currentMonth, currentYear)) {
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
                        .avgConcurrent(statistic.getAvgConcurrent().doubleValue())
                        .growthRate(statistic.getGrowthRate().doubleValue())
                        .trendingScore(statistic.getTrendingScore().doubleValue())
                        .build();
                results.add(record);
            }
            try {
                topMonthlyGameRepository.saveAll(results);
            } catch (Exception e) {
                log.error("Failed to update monthly trending records", e);
                throw new RuntimeException("Failed to update monthly trending records", e);
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Monthly trending records updated in {} ms for {}/{}", endTime - startTime, currentMonth, currentYear);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "GMT+7")
    public void updateDailyTrending() {
        log.info("Updating daily trending records...");
        long startTime = System.currentTimeMillis();
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getDailyTrendingStatistics();
        redisService.save("dailyTrending", trendingStatistics);
        long endTime = System.currentTimeMillis();
        log.info("Daily trending records updated in {} ms", endTime - startTime);
    }

}

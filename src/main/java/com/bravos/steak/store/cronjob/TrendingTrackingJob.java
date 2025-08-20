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
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getWeeklyTrendingStatistics();
        redisService.save("weeklyTrending", trendingStatistics);
        log.info("Weekly trending records updated");
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "GMT+7")
    @Transactional
    public void updateMonthlyTrending() {
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getMonthlyTrendingStatistics();
        redisService.save("monthlyTrending", trendingStatistics);
        int currentMonth = LocalDate.now().getMonth().getValue();
        int currentYear = LocalDate.now().getYear();

        if(!topMonthlyGameRepository.existsByTime(currentMonth, currentYear)) {
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
            try {
                topMonthlyGameRepository.saveAll(results);
            } catch (Exception e) {
                log.error("Failed to update monthly trending records", e);
                throw new RuntimeException("Failed to update monthly trending records", e);
            }
            log.info("Monthly trending records updated for {}/{}", currentMonth, currentYear);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "GMT+7")
    public void updateDailyTrending() {
        List<TrendingStatistic> trendingStatistics = trendingStatisticRepository.getDailyTrendingStatistics();
        redisService.save("dailyTrending", trendingStatistics);
        log.info("Daily trending records updated");
    }

}

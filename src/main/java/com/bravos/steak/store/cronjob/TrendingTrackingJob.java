package com.bravos.steak.store.cronjob;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrendingTrackingJob {

    @Scheduled(cron = "0 0 1 * * Mon")
    public void updateWeeklyTrending() {

    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void updateMonthlyTrending() {

    }

    @Scheduled(cron = "0 0 1 * * *")
    public void updateDailyTrending() {

    }

}

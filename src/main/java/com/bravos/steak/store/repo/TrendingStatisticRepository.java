package com.bravos.steak.store.repo;

import com.bravos.steak.store.repo.injection.TrendingStatistic;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class TrendingStatisticRepository {

    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public TrendingStatisticRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<TrendingStatistic> getWeeklyTrendingStatistics() {
        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .atZone(ZoneId.of("GMT+7"))
                .toLocalDateTime();
        String sql = "SELECT * FROM proc_weekly_trending(?)";
        return queryTrendingStatistics(sql, startOfWeek);
    }

    public List<TrendingStatistic> getMonthlyTrendingStatistics() {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .atZone(ZoneId.of("GMT+7"))
                .toLocalDateTime();
        String sql = "SELECT * FROM proc_monthly_trending(?)";
        return queryTrendingStatistics(sql, startOfMonth);
    }

    public List<TrendingStatistic> getDailyTrendingStatistics() {
        LocalDateTime asTime = LocalDateTime.now()
                .toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.of("GMT+7"))
                .toLocalDateTime();
        String sql = "SELECT * FROM proc_daily_trending(?)";
        return queryTrendingStatistics(sql, asTime);
    }

    private List<TrendingStatistic> queryTrendingStatistics(String sql, LocalDateTime asTime) {
        Query query = entityManager.createNativeQuery(sql, TrendingStatistic.class);
        query.setParameter(1, asTime);
        return objectMapper.convertValue(query.getResultList(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrendingStatistic.class));
    }

}

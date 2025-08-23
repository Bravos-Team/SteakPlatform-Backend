package com.bravos.steak.administration.repo;

import com.bravos.steak.administration.model.response.StatisticResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminStatisticRepository {

    @PersistenceContext
    public EntityManager entityManager;

    public List<StatisticResponse> getRevenueStatisticByYear() {
        String sql = "SELECT * FROM gettotalrevenuebyyear()";
        Query query = entityManager.createNativeQuery(sql);
        return buildResultMap(query);
    }

    public List<StatisticResponse> getRevenueStatisticByMonth(int year) {
        String sql = "SELECT * FROM gettotalrevenuebymonth(:year)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("year", year);
        return buildResultMap(query);
    }

    public List<StatisticResponse> getRevenueStatisticByDay(int month, int year) {
        String sql = "SELECT * FROM gettotalrevenuebyday(:year, :month)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("month", month);
        query.setParameter("year", year);
        return buildResultMap(query);
    }

    private List<StatisticResponse> buildResultMap(Query query) {
        var result = query.getResultList();
        List<StatisticResponse> res = new ArrayList<>(result.size());
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            StatisticResponse statisticResponse = StatisticResponse.builder()
                    .name((String) arr[0])
                    .value(arr[1])
                    .build();
            res.add(statisticResponse);
        }
        return res;
    }

}

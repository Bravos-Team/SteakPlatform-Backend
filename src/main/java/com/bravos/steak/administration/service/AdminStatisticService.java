package com.bravos.steak.administration.service;

import com.bravos.steak.administration.model.response.StatisticResponse;

import java.util.List;

public interface AdminStatisticService {

    List<StatisticResponse> getRevenueStatisticByYear();

    List<StatisticResponse> getRevenueStatisticByMonth(int year);

    List<StatisticResponse> getRevenueStatisticByDay(int month, int year);

}

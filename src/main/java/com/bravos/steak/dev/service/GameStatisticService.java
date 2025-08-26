package com.bravos.steak.dev.service;

import com.bravos.steak.common.model.CustomPage;
import com.bravos.steak.dev.model.response.GameStatisticItem;

public interface GameStatisticService {

    CustomPage<GameStatisticItem> getGameStatisticsRevenue(Integer month, Integer year, int page, int pageSize);

}

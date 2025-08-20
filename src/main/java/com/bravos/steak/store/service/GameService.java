package com.bravos.steak.store.service;

import com.bravos.steak.common.model.CustomPage;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.request.FilterQuery;
import com.bravos.steak.store.model.response.*;
import com.bravos.steak.store.repo.injection.TrendingStatistic;

import java.util.List;
import java.util.Set;

public interface GameService {

    CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize);

    CustomPage<GameListItem> getFilteredGames(FilterQuery filterQuery);

    CustomPage<GameListItem> getNewestGames(int page, int pageSize);

    CustomPage<GameListItem> getComingSoonGames(int page, int pageSize);

    CustomPage<GameListItem> getTopMostPlayedGames(int page, int pageSize);

    GameStoreDetail getGameStoreDetails(Long gameId);

    DownloadResponse getGameDownloadUrl(Long gameId);

    Set<Genre> getAllGenres();

    Set<Tag> getAllTags();

    GameStoreDetail invalidateAndGetGameStoreDetails(Long gameId);

    List<TrendingStatistic> getWeeklyTrendingStatistics();

    List<TrendingStatistic> getMonthlyTrendingStatistics();

    List<TrendingStatistic> getDailyTrendingStatistics();

    List<GameRankingListItem> getCurrentDayGameRankingList();

    List<GameRankingListItem> getCurrentWeekGameRankingList();

    List<GameRankingListItem> getCurrentMonthGameRankingList();

}

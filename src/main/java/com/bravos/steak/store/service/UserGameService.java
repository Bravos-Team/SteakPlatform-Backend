package com.bravos.steak.store.service;

import java.util.List;
import java.util.Set;

public interface UserGameService {

    void updateUserGame(long userId, long gameId, long playTime, long currentTime);

    void increaseCurrentPlayingGame(long gameId);

    void decreaseCurrentPlayingGame(long gameId);

    long getCurrentPlayingGameCount(long gameId);

    void savePlayingCountJob();

    Set<Long> getTopPlayedGames(long start, long end);

    long countTotalPlayedGames();

}

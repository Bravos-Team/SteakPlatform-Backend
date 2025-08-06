package com.bravos.steak.store.service;

public interface UserGameService {

    void updateUserGame(long userId, long gameId, long playTime, long currentTime);

    void increaseCurrentPlayingGame(long gameId);

    void decreaseCurrentPlayingGame(long gameId);

    long getCurrentPlayingGameCount(long gameId);

    void savePlayingCountJob();

}

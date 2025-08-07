package com.bravos.steak.store.listener;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.store.model.event.CounterEvent;
import com.bravos.steak.store.service.UserGameService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GameTrackingListener {

    private final UserGameService userGameService;

    public GameTrackingListener(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @EventListener
    @Async
    public void increasePlayingCount(CounterEvent.IncreasePlayingCountEvent event) {
        userGameService.increaseCurrentPlayingGame(event.getGameId());
        userGameService.updateUserGame(event.getUserId(), event.getGameId(), 0, DateTimeHelper.currentTimeMillis());
    }

    @EventListener
    @Async
    public void decreasePlayingCount(CounterEvent.DecreasePlayingCountEvent event) {
        userGameService.decreaseCurrentPlayingGame(event.getGameId());
        userGameService.updateUserGame(event.getUserId(), event.getGameId(), event.getPlayTime(), DateTimeHelper.currentTimeMillis());
    }

}

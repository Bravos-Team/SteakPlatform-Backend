package com.bravos.steak.store.listener;

import com.bravos.steak.store.model.event.CounterEvent;
import com.bravos.steak.store.service.UserGameService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PlayingCountListener {

    private final UserGameService userGameService;

    public PlayingCountListener(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @Async
    @EventListener
    public void increasePlayingCountEvent(CounterEvent.IncreasePlayingCountEvent event) {
        userGameService.increaseCurrentPlayingGame(event.getGameId());
    }

    @Async
    @EventListener
    public void decreasePlayingCountEvent(CounterEvent.DecreasePlayingCountEvent event) {
        userGameService.decreaseCurrentPlayingGame(event.getGameId());
        userGameService.updateUserGame(event.getUserId(), event.getGameId(), event.getPlayTime(), System.currentTimeMillis());
    }

}

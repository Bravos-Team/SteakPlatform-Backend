package com.bravos.steak.store.model.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class CounterEvent {

    @Getter
    @Setter
    public static class IncreasePlayingCountEvent extends ApplicationEvent {

        private final long gameId;
        private final long userId;

        public IncreasePlayingCountEvent(Object source, long gameId, long userId) {
            super(source);
            this.gameId = gameId;
            this.userId = userId;
        }

    }

    @Getter
    @Setter
    public static class DecreasePlayingCountEvent extends ApplicationEvent {

        private final long gameId;
        private final long userId;
        private final long playTime;

        public DecreasePlayingCountEvent(Object source, long gameId, long userId, long playTime) {
            super(source);
            this.gameId = gameId;
            this.userId = userId;
            this.playTime = playTime;
        }

    }

}

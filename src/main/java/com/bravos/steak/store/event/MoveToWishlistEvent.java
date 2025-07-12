package com.bravos.steak.store.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MoveToWishlistEvent extends ApplicationEvent {

    @Getter
    private final Long gameId;

    public MoveToWishlistEvent(Object source, Long gameId) {
        super(source);
        this.gameId = gameId;
    }

}

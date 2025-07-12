package com.bravos.steak.store.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MoveToCartEvent extends ApplicationEvent {

    @Getter
    private final Long gameId;

    public MoveToCartEvent(Object source, Long gameId) {
        super(source);
        this.gameId = gameId;
    }

}

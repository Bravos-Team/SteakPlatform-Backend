package com.bravos.steak.store.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PaymentSuccessEvent extends ApplicationEvent {

    @Getter
    private final Long orderId;

    public PaymentSuccessEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

}

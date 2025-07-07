package com.bravos.steak.store.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PaymentFailureEvent extends ApplicationEvent {

    @Getter
    private final Long orderId;

    @Getter
    private final String reason;

    public PaymentFailureEvent(Object source, Long orderId, String reason) {
        super(source);
        this.orderId = orderId;
        this.reason = reason;
    }

}

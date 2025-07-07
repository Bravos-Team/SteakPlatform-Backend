package com.bravos.steak.store.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    Long orderId;

    Double amount;

    String ipAddress;

    String locale;

    String orderInfo;

    String returnUrl;

}

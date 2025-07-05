package com.bravos.steak.common.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfo {

    private String tmnCode;

    private String secretKey;

    private String paymentEndpoint;

}

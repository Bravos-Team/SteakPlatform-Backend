package com.bravos.steak.store.model.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateOrderResponse {

    private String paymentUrl;

}

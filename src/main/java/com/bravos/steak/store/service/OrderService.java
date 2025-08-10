package com.bravos.steak.store.service;

import com.bravos.steak.store.model.request.CreateOrderRequest;
import com.bravos.steak.store.model.response.CreateOrderResponse;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    void handleSuccessfulPayment(Long orderId);

    void handleFailedPayment(Long orderId, String reason);

    void handleFreeOrder(Long orderId);

}

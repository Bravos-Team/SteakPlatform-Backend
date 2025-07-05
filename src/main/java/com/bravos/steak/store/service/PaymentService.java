package com.bravos.steak.store.service;

import com.bravos.steak.store.model.request.CreatePaymentRequest;

public interface PaymentService {

    String createPaymentUrl(CreatePaymentRequest request);

}

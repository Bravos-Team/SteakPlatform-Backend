package com.bravos.steak.store.service;

import com.bravos.steak.store.model.request.CreatePaymentRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {

    String createPaymentUrl(CreatePaymentRequest request);

    String handleVnpIpn(HttpServletRequest httpServletRequest);

}

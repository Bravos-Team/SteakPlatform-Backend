package com.bravos.steak.store.controller;

import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.store.model.request.CreatePaymentRequest;
import com.bravos.steak.store.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PublisherController
@RequestMapping("/api/v1/dev/test")
public class TestController {

    private final PaymentService paymentService;
    private final SnowflakeGenerator snowflakeGenerator;

    public TestController(PaymentService paymentService, SnowflakeGenerator snowflakeGenerator) {
        this.paymentService = paymentService;
        this.snowflakeGenerator = snowflakeGenerator;
    }

    @GetMapping("/payment")
    public String test() {
        return paymentService.createPaymentUrl(new CreatePaymentRequest(
                snowflakeGenerator.generateId(),
                10000.00d,
                "171.226.128.153",
                "vi",
                "Thanh toan don hang 1234567890",
                "http://localhost:8888/ipn/vnpay"
        ));
    }

}

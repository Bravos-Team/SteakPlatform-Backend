package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ipn/vnpay")
public class VnpayIpnController {

    private final PaymentService paymentService;

    public VnpayIpnController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public String handleVnpayIpn(HttpServletRequest httpServletRequest) {
        return "redirect:" + System.getProperty("BASE_URL_FRONTEND") + paymentService.handleVnpIpn(httpServletRequest);
    }

}

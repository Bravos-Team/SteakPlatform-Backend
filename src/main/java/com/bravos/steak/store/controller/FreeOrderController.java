package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/store/private/free-order")
public class FreeOrderController {

    private final OrderService orderService;

    public FreeOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public String getFreeOrderPage(@PathVariable Long orderId) {
        orderService.handleFreeOrder(orderId);
        return "redirect:" + System.getProperty("BASE_URL_FRONTEND").concat("/payment/success?orderId=") + orderId;
    }

}

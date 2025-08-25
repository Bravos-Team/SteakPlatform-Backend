package com.bravos.steak.store.listener;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.helper.*;
import com.bravos.steak.store.entity.Order;
import com.bravos.steak.store.event.PaymentSuccessEvent;
import com.bravos.steak.store.repo.OrderRepository;
import com.bravos.steak.store.service.GameService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderListener {

    private final OrderRepository orderRepository;

    private static final String PAYMENT_METHOD = "VNPAY";
    private final GameService gameService;
    private final EmailService emailService;

    public OrderListener(OrderRepository orderRepository, GameService gameService, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.gameService = gameService;
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void sendInvoiceEmail(PaymentSuccessEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + event.getOrderId()));
        List<Long> gameIds = order.getOrderDetails().stream()
                .map(od -> od.getGame().getId())
                .toList();
        List<GameItem> items = new ArrayList<>(gameService.getGameListByIds(gameIds).stream()
                .map(g -> new GameItem(
                        String.valueOf(g.getId()),
                        g.getName(),
                        g.getPrice(),
                        g.getPrice(),
                        0
                )).toList());
        Invoice invoice = new Invoice(
                String.valueOf(event.getOrderId()),
                DateTimeHelper.toLocalDateTime(order.getCreatedAt()),
                PAYMENT_METHOD,
                Customer.builder()
                        .id(String.valueOf(order.getUserAccount().getId()))
                        .email(order.getUserAccount().getEmail())
                        .name(order.getUserAccount().getUsername())
                        .build(),
                items
        );
        String emailContent = InvoiceEmailGenerator.generateInvoiceEmail(invoice);
        emailService.sendEmail(EmailPayload.builder()
                        .to(order.getUserAccount().getEmail())
                        .subject("Your Invoice from Bravos Steak")
                        .htmlPart(emailContent)
                .build());
    }

}

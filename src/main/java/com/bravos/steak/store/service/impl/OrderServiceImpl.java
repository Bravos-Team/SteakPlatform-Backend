package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.*;
import com.bravos.steak.store.event.PaymentFailureEvent;
import com.bravos.steak.store.event.PaymentSuccessEvent;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.enums.OrderStatus;
import com.bravos.steak.store.model.request.CreateOrderRequest;
import com.bravos.steak.store.model.request.CreatePaymentRequest;
import com.bravos.steak.store.model.response.CreateOrderResponse;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.OrderDetailsRepository;
import com.bravos.steak.store.repo.OrderRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.store.repo.injection.GameIdStatusPrice;
import com.bravos.steak.store.service.OrderService;
import com.bravos.steak.store.service.PaymentService;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final UserGameRepository userGameRepository;
    private final PaymentService paymentService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final SessionService sessionService;
    private final GameRepository gameRepository;
    private final HttpServletRequest httpServletRequest;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository,
                            UserGameRepository userGameRepository, PaymentService paymentService,
                            SnowflakeGenerator snowflakeGenerator, SessionService sessionService,
                            GameRepository gameRepository, HttpServletRequest httpServletRequest) {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.userGameRepository = userGameRepository;
        this.paymentService = paymentService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.sessionService = sessionService;
        this.gameRepository = gameRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @EventListener
    @Transactional
    @org.springframework.core.annotation.Order(1)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        handleSuccessfulPayment(event.getOrderId());
    }

    @EventListener
    @Transactional
    public void onPaymentFailure(PaymentFailureEvent event) {
        handleFailedPayment(event.getOrderId(), event.getReason());
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Long[] listGameId = request.getGameIds();
        if(listGameId == null || listGameId.length == 0) {
            throw new BadRequestException("Game IDs cannot be empty");
        }

        JwtAuthentication authentication = sessionService.getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        Order order = Order.builder()
                .id(snowflakeGenerator.generateId())
                .status(OrderStatus.UNPAID)
                .userAccount(UserAccount.builder().id(userId).build())
                .build();

        try {
            order = orderRepository.save(order);
        } catch (Exception e) {
            log.error("Failed to create order for user: {}", userId, e);
            throw new RuntimeException("Failed to create order for user: " + userId);
        }

        List<GameIdStatusPrice> gameIdStatusPrice = gameRepository.findGameIdStatusPrice(listGameId);

        if(gameIdStatusPrice.isEmpty()) {
            throw new BadRequestException("No valid games found for the provided game IDs");
        }

        StringBuilder errorMessage = new StringBuilder();
        for (var gameInfo : gameIdStatusPrice) {
            if (gameInfo.getStatus() != GameStatus.OPENING) {
                errorMessage.append("Game ID ").append(gameInfo.getId())
                        .append(" is not available for purchase. \n");
            }
        }

        if (!errorMessage.isEmpty()) {
            throw new BadRequestException(errorMessage.toString());
        }

        List<OrderDetails> orderDetailsList = new ArrayList<>();
        for (var gameInfo : gameIdStatusPrice) {
            OrderDetails orderDetails = OrderDetails.builder()
                    .id(snowflakeGenerator.generateId())
                    .order(order)
                    .game(Game.builder().id(gameInfo.getId()).build())
                    .price(gameInfo.getPrice())
                    .build();
            orderDetailsList.add(orderDetails);
        }

        try {
            orderDetailsList = orderDetailsRepository.saveAllAndFlush(orderDetailsList);
        } catch (Exception e) {
            log.error("Failed to save order details for order: {}", order.getId(), e);
            throw new RuntimeException("Failed to save order details for order: " + order.getId());
        }
        CreatePaymentRequest paymentRequest = CreatePaymentRequest.builder()
                .orderInfo("Payment for order ID: " + order.getId())
                .orderId(order.getId())
                .locale("en")
                .ipAddress(getIpAddress())
                .amount(orderDetailsList.stream().mapToDouble(o -> o.getPrice().doubleValue()).sum())
                .returnUrl(System.getProperty("DOMAIN") + "/ipn/vnpay")
                .build();

        String paymentUrl = paymentService.createPaymentUrl(paymentRequest);
        return CreateOrderResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public void handleSuccessfulPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null) {
            throw new RuntimeException("Order not found for transaction reference: " + orderId);
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BadRequestException("Order is not in unpaid status, cannot process payment");
        }
        order.setStatus(OrderStatus.SUCCESS);
        order.setUpdatedAt(DateTimeHelper.currentTimeMillis());
        try {
            order = orderRepository.save(order);
        } catch (Exception e) {
            log.error("Failed to update order status for transaction reference: {}", orderId, e);
            throw new RuntimeException("Failed to update order status for transaction reference: " + orderId);
        }
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrder_Id(order.getId());
        List<UserGame> userGames = new ArrayList<>();
        for (OrderDetails orderDetails : orderDetailsList) {
            UserGame userGame = UserGame.builder()
                    .id(new UserGameId(order.getUserAccount().getId(), orderDetails.getGame().getId()))
                    .user(order.getUserAccount())
                    .game(orderDetails.getGame())
                    .ownedAt(DateTimeHelper.currentTimeMillis())
                    .build();
            userGames.add(userGame);
        }
        try {
            userGameRepository.saveAll(userGames);
        } catch (Exception e) {
            log.error("Failed to save user games for order: {}", order.getId(), e);
            throw new RuntimeException("Failed to save user games for order: " + order.getId());
        }
    }

    @Override
    public void handleFailedPayment(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null) {
            throw new RuntimeException("Order not found for transaction reference: " + orderId);
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BadRequestException("Order is not in unpaid status, cannot process payment failure");
        }
        order.setStatus(OrderStatus.FAILED);
        order.setUpdatedAt(DateTimeHelper.currentTimeMillis());
        order.setMessage(reason);
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Failed to update order status for transaction reference: {}", orderId, e);
            throw new RuntimeException("Failed to update order status for transaction reference: " + orderId);
        }
    }

    private String getIpAddress() {
        String ipAddress = httpServletRequest.getHeader("X-Real-IP");
        return ipAddress != null ? ipAddress : httpServletRequest.getRemoteAddr();
    }

}

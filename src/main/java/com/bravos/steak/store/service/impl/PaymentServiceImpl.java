package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.model.PaymentInfo;
import com.bravos.steak.common.service.encryption.Hmac512Service;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.store.event.PaymentFailureEvent;
import com.bravos.steak.store.event.PaymentSuccessEvent;
import com.bravos.steak.store.model.request.CreatePaymentRequest;
import com.bravos.steak.store.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String vnpayVersion = "2.1.0";
    private static final String vnpayCommand = "pay";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Map<String, String> vnpayStatusMessages = new HashMap<>();
    private final PaymentInfo paymentInfo;
    private final Hmac512Service hmac512Service;
    private final RedisService redisService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentServiceImpl(PaymentInfo paymentInfo, Hmac512Service hmac512Service,
                              RedisService redisService, ApplicationEventPublisher applicationEventPublisher) {
        this.paymentInfo = paymentInfo;
        this.hmac512Service = hmac512Service;

        vnpayStatusMessages.put("00", "Transaction successful");
        vnpayStatusMessages.put("07", "Money deducted successfully. Transaction is suspected (related to fraud or unusual activity).");
        vnpayStatusMessages.put("09", "Transaction failed: The customer's card/account has not registered for Internet Banking service at the bank.");
        vnpayStatusMessages.put("10", "Transaction failed: The customer entered incorrect card/account information more than 3 times.");
        vnpayStatusMessages.put("11", "Transaction failed: Payment waiting time has expired. Please try the transaction again.");
        vnpayStatusMessages.put("12", "Transaction failed: The customer's card/account is locked.");
        vnpayStatusMessages.put("13", "Transaction failed: You entered the wrong OTP authentication password. Please try the transaction again.");
        vnpayStatusMessages.put("24", "Transaction failed: Customer canceled the transaction.");
        vnpayStatusMessages.put("51", "Transaction failed: Your account does not have enough balance to complete the transaction.");
        vnpayStatusMessages.put("65", "Transaction failed: Your account has exceeded the daily transaction limit.");
        vnpayStatusMessages.put("75", "The payment bank is under maintenance.");
        vnpayStatusMessages.put("79", "Transaction failed: You entered the wrong payment password too many times. Please try the transaction again.");

        vnpayStatusMessages.forEach((key, value) -> vnpayStatusMessages.put(key, URLEncoder.encode(value, StandardCharsets.UTF_8)));

        this.redisService = redisService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String createPaymentUrl(CreatePaymentRequest request) {
        LocalDateTime now = DateTimeHelper.now();
        Map<String, String> vnpayParams = new TreeMap<>(String::compareTo);
        long price = request.getAmount() * 100 < 0 ? 0 : (long) (request.getAmount() * 100);
        vnpayParams.put("vnp_Version", vnpayVersion);
        vnpayParams.put("vnp_Command", vnpayCommand);
        vnpayParams.put("vnp_TmnCode", paymentInfo.getTmnCode());
        vnpayParams.put("vnp_Amount", String.valueOf(price));
        vnpayParams.put("vnp_CreateDate", formatter.format(now));
        vnpayParams.put("vnp_CurrCode", "VND");
        vnpayParams.put("vnp_IpAddr", request.getIpAddress());
        vnpayParams.put("vnp_Locale", request.getLocale());
        vnpayParams.put("vnp_OrderInfo", request.getOrderInfo());
        vnpayParams.put("vnp_OrderType", "250000");
        vnpayParams.put("vnp_ReturnUrl", request.getReturnUrl());
        vnpayParams.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));
        vnpayParams.put("vnp_TxnRef", String.valueOf(request.getOrderId()));

        StringJoiner queryString = new StringJoiner("&");
        StringJoiner hashData = new StringJoiner("&");
        for (var entry : vnpayParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isBlank()) {
                String fieldName = URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII);
                String fieldValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII);
                queryString.add(fieldName + "=" + fieldValue);
                hashData.add(entry.getKey() + "=" + fieldValue);
            }
        }

        String secureHash = generateSecureHash(hashData.toString());
        queryString.add("vnp_SecureHash=" + secureHash);

        return paymentInfo.getPaymentEndpoint() + "?" + queryString;
    }

    @Override
    public String handleVnpIpn(HttpServletRequest httpServletRequest) {
        Map<String, String[]> params = new TreeMap<>(httpServletRequest.getParameterMap());
        String secureHash = params.remove("vnp_SecureHash")[0];
        String vnpTxnRef = params.get("vnp_TxnRef")[0];

        if (secureHash == null || secureHash.isBlank()) {
            return "/payment/error?message=" +
                    URLEncoder.encode("Missing secure hash in IPN request", StandardCharsets.UTF_8);
        }

        LocalDateTime completedDate = LocalDateTime.parse(params.get("vnp_PayDate")[0], formatter);
        LocalDateTime now = DateTimeHelper.now();

        if(!completedDate.isBefore(now.plusHours(2))) {
            return "/payment/error?message=" + URLEncoder.encode("Transaction has expired", StandardCharsets.UTF_8);
        }

        StringJoiner hashData = new StringJoiner("&");
        params.forEach((key, value) -> {
            if (value != null && value.length > 0 && !value[0].isBlank()) {
                hashData.add(key + "=" + URLEncoder.encode(value[0], StandardCharsets.US_ASCII));
            }
        });
        if (hmac512Service.verifyData(hashData.toString(), paymentInfo.getSecretKey(), secureHash)) {
            if (isLockTransaction(vnpTxnRef)) {
                return "/payment/error?message=" +
                        URLEncoder.encode("Transaction is being processed, please wait", StandardCharsets.UTF_8);
            }
            if (isTransactionCompleted(vnpTxnRef)) {
                return "/payment/error?message=" +
                        URLEncoder.encode("Transaction has already been processed", StandardCharsets.UTF_8);
            }
            try {
                String vnpResponseCode = params.get("vnp_ResponseCode")[0];
                if ("00".equals(vnpResponseCode)) {
                    applicationEventPublisher.publishEvent(new PaymentSuccessEvent(this, Long.parseLong(vnpTxnRef)));
                    return "/payment/success?orderId=" + vnpTxnRef;
                } else {
                    String message = vnpayStatusMessages.getOrDefault(vnpResponseCode, "Unknown error");
                    applicationEventPublisher.publishEvent(new PaymentFailureEvent(
                            this, Long.parseLong(vnpTxnRef), message));
                    return "/payment/error?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
                }
            } finally {
                saveCompletedTransaction(vnpTxnRef);
                redisService.delete("lock:" + vnpTxnRef);
            }
        }
        return "/payment/error?message=" +
                URLEncoder.encode("IPN request signature verification failed", StandardCharsets.UTF_8);
    }

    private String generateSecureHash(String hashData) {
        return hmac512Service.signData(hashData, paymentInfo.getSecretKey());
    }

    private boolean isLockTransaction(String vnpTxnRef) {
        String lockKey = "lock:" + vnpTxnRef;
        return !redisService.saveIfAbsent(lockKey, UUID.randomUUID(), 15, TimeUnit.MINUTES);
    }

    private void saveCompletedTransaction(String vnpTxnRef) {
        String key = "completed:" + vnpTxnRef;
        redisService.saveIfAbsent(key, true, 2, TimeUnit.HOURS);
    }

    private boolean isTransactionCompleted(String vnpTxnRef) {
        String key = "completed:" + vnpTxnRef;
        return redisService.hasKey(key);
    }

}


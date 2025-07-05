package com.bravos.steak.common.configuration;

import com.bravos.steak.common.model.PaymentInfo;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PaymentConfiguration {

    private final KeyVaultService keyVaultService;

    public PaymentConfiguration(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Profile("dev")
    public PaymentInfo devPaymentInfo() {
        return PaymentInfo.builder()
                .tmnCode(System.getProperty("PAYMENT_TMN_CODE"))
                .secretKey(System.getProperty("PAYMENT_SECRET_KEY"))
                .paymentEndpoint(System.getProperty("PAYMENT_ENDPOINT"))
                .build();
    }

    @Profile("prod")
    public PaymentInfo prodPaymentInfo() {
        return PaymentInfo.builder()
                .tmnCode(keyVaultService.getSecretKey(System.getProperty("PAYMENT_TMN_CODE")))
                .secretKey(keyVaultService.getSecretKey(System.getProperty("PAYMENT_SECRET_KEY")))
                .paymentEndpoint(keyVaultService.getSecretKey(System.getProperty("PAYMENT_ENDPOINT")))
                .build();
    }


}

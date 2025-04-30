package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.service.encryption.Hmac512Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
public class Hmac512ServiceImpl implements Hmac512Service {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String signData(String data, String secret) {
        try {
            byte[] keyBytes = secret.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyData(String data, String secret, String signature) {
        try {
            return signData(data, secret).equals(signature);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String generateSecretKey() {
        byte[] key = new byte[64];
        SECURE_RANDOM.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

}

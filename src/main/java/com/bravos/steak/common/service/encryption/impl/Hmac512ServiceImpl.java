package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.service.encryption.Hmac512Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

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
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error signing data with HMAC SHA-512", e);
        }
    }

    @Override
    public boolean verifyData(String data, String secret, String signature) {
        try {
            String signData = signData(data, secret);
            return MessageDigest.isEqual(
                    HexFormat.of().parseHex(signData),
                    HexFormat.of().parseHex(signature));
        } catch (Exception e) {
            log.error("Error verifying HMAC SHA-512 signature", e);
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

package com.bravos.steak.common.service.encryption;

public interface Hmac512Service {

    String signData(String data, String secret);

    boolean verifyData(String data, String secret, String signature);

    String generateSecretKey();

}

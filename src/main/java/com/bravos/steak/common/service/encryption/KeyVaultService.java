package com.bravos.steak.common.service.encryption;

public interface KeyVaultService {

    String getSecretKey(String keyName);
}

package com.bravos.steak.common.service.encryption.impl;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KeyVaultServiceImpl implements KeyVaultService {

    private final SecretClient secretClient;

    public KeyVaultServiceImpl(SecretClient secretClient) {
        this.secretClient = secretClient;
    }

    @Override
    public String getSecretKey(String keyName) {
        try {
            KeyVaultSecret keyVaultSecret = secretClient.getSecret(keyName);
            return keyVaultSecret.getValue();
        } catch (Exception e) {
            log.error("Cannot get secretKey: {}",e.getMessage());
            throw new RuntimeException("Cannot get secret key");
        }
    }

}

package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.service.encryption.AesEncryptionService;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.stereotype.Service;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    public final String secretKey;

    private final AesEncryptionService aesEncryptionService;
    private final KeyVaultService keyVaultService;

    public EncryptionServiceImpl(AesEncryptionService aesEncryptionService, KeyVaultService keyVaultService) {
        this.aesEncryptionService = aesEncryptionService;
        this.keyVaultService = keyVaultService;
        this.secretKey = keyVaultService.getSecretKey("steak-secret-key");
    }

    @Override
    public String aesEncrypt(String data) {
        return aesEncryptionService.encrypt(data, secretKey);
    }

    @Override
    public String aesDecrypt(String encryptedData) {
        return aesEncryptionService.decrypt(encryptedData, secretKey);
    }

}

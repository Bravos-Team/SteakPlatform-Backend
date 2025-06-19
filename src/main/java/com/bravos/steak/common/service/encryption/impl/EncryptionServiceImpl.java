package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.model.SecretKeyX;
import com.bravos.steak.common.service.encryption.AesEncryptionService;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.stereotype.Service;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private final AesEncryptionService aesEncryptionService;
    private final SecretKeyX secretKeyX;

    public EncryptionServiceImpl(AesEncryptionService aesEncryptionService, SecretKeyX secretKeyX) {
        this.aesEncryptionService = aesEncryptionService;
        this.secretKeyX = secretKeyX;
    }

    @Override
    public String aesEncrypt(String data) {
        return aesEncryptionService.encrypt(data, secretKeyX.getSecretKey());
    }

    @Override
    public String aesDecrypt(String encryptedData) {
        return aesEncryptionService.decrypt(encryptedData, secretKeyX.getSecretKey());
    }

}

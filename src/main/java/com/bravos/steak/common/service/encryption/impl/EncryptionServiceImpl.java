package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.service.encryption.AesEncryptionService;
import com.bravos.steak.common.service.encryption.EncryptionService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    public static final String SECRET_KEY = System.getProperty("SECRET_KEY");

    private final AesEncryptionService aesEncryptionService;

    public EncryptionServiceImpl(AesEncryptionService aesEncryptionService) {
        this.aesEncryptionService = aesEncryptionService;
    }

    @Override
    public String aesEncrypt(String data) {
        return aesEncryptionService.encrypt(data,SECRET_KEY);
    }

    @Override
    public String aesDecrypt(String encryptedData) {
        return aesEncryptionService.decrypt(encryptedData,SECRET_KEY);
    }

}

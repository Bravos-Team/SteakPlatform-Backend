package com.bravos.steak.common.service.encryption;

public interface EncryptionService {

    String aesEncrypt(String data);

    String aesDecrypt(String encryptedData);

}

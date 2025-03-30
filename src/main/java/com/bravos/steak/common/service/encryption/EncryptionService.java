package com.bravos.steak.common.service.encryption;

import javax.crypto.SecretKey;

public interface EncryptionService {

    String aesEncrypt(String data);

    String aesDecrypt(String encryptedData);

}

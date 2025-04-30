package com.bravos.steak.common.service.encryption;

import javax.crypto.SecretKey;

public interface AesEncryptionService {

    /**
     * Dùng để mã hóa dữ liệu
     * @param plainText dữ liệu thô
     * @param secretKey mã bí mật
     * @return dữ liệu đã mã hóa
     */
    String encrypt(String plainText, String secretKey);

    String encrypt(String plainText, SecretKey secretKey);

    /**
     * Giải mã dữ liệu
     * @param encryptedText dữ liệu đã mã hóa
     * @param secretKey mã bí mật
     * @return dữ liệu đã giải mã
     */
    String decrypt(String encryptedText, String secretKey);

    String decrypt(String encryptedText, SecretKey secretKey);

    /**
     * Tạo mã bí mật ngẫu nhiên
     * @return mã bí mật Base64
     */
    String generateSecretKey();

    SecretKey convertSecretKey(String secretKey);
}

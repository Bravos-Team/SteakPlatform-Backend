package com.bravos.steak.common.service.encryption;

public interface AesEncryptionService {

    /**
     * Dùng để mã hóa dữ liệu
     * @param plainText dữ liệu thô
     * @param secretKey mã bí mật
     * @return dữ liệu đã mã hóa
     */
    String encrypt(String plainText, String secretKey);

    /**
     * Giải mã dữ liệu
     * @param cipherText dữ liệu đã mã hóa
     * @param secretKey mã bí mật
     * @return dữ liệu đã giải mã
     */
    String decrypt(String cipherText, String secretKey);

    /**
     * Tạo mã bí mật ngẫu nhiên
     * @return mã bí mật Base64
     */
    String generateSecretKey();

}

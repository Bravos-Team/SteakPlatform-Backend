package com.bravos.steak.common.service.encryption;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public interface RSAService {

    String encrypt(String plainText, String publicKey);

    String encrypt(String plainText, PublicKey publicKey);

    String encrypt(byte[] data, String publicKey);

    String encrypt(byte[] data, PublicKey publicKey);

    String decrypt(String cipherText, String privateKey);

    String decrypt(String cipherText, PrivateKey privateKey);

    String decrypt(byte[] cipherData, String privateKey);

    String decrypt(byte[] cipherData, PrivateKey privateKey);

    String getSignatureData(String data, String privateKey);

    String getSignatureData(String data, PrivateKey privateKey);

    boolean verifyData(String data, String signature, String publicKey);

    String generatePrivateKey();

    String generatePublicKey(String privateKey);

    KeyPair generateKeyPair();

    String getPrivateKeyFromKeyPair(KeyPair keyPair);

    String getPublicKeyFromKeyPair(KeyPair keyPair);

    default PublicKey convertPublicKey(String publicKey) {
        try {
            publicKey = publicKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\n", "");
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid public key");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid algorithm");
        }
    }

    default PrivateKey convertPrivateKey(String privateKey) {
        try {
            privateKey = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\n", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid private key");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid algorithm");
        }
    }

}

package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.service.encryption.RSAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class RSAServiceImpl implements RSAService {

    private static final String ALGORITHM = "RSA";

    @Override
    public String encrypt(String plainText, String publicKey) {
        return encrypt(plainText,convertPublicKey(publicKey));
    }

    @Override
    public String encrypt(String plainText, PublicKey publicKey) {
        return encrypt(plainText.getBytes(),publicKey);
    }

    @Override
    public String encrypt(byte[] data, String publicKey) {
        return encrypt(data,convertPublicKey(publicKey));
    }

    @Override
    public String encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error when encrypt data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when encrypt data");
        }
    }

    @Override
    public String decrypt(String cipherData, String privateKey) {
        return decrypt(cipherData,convertPrivateKey(privateKey));
    }

    @Override
    public String decrypt(String cipherData, PrivateKey privateKey) {
        return decrypt(Base64.getDecoder().decode(cipherData),privateKey);
    }

    @Override
    public String decrypt(byte[] cipherData, String privateKey) {
        return decrypt(cipherData,convertPrivateKey(privateKey));
    }

    @Override
    public String decrypt(byte[] cipherData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(cipherData);
            return new String(decryptedData);
        } catch (Exception e) {
            log.error("Error when decrypt data: {}", e.getMessage(), e);
            throw new RuntimeException("Error when decrypt data");
        }
    }

    @Override
    public String getSignatureData(String data, String privateKey) {
        return getSignatureData(data,convertPrivateKey(privateKey));
    }

    @Override
    public String getSignatureData(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] signDataBytes = signature.sign();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signDataBytes);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            log.error("Error when get signature data: {}",e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean verifyData(String data, String signatureData, String publicKey) {
        return verifyData(data,signatureData,convertPublicKey(publicKey));
    }

    @Override
    public boolean verifyData(String data, String signatureData, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            byte[] signedDataBytes = Base64.getUrlDecoder().decode(signatureData);
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            return signature.verify(signedDataBytes);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            log.warn("Failed when verify data: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String generatePrivateKey() {
        try {
            KeyPair keyPair = this.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            return Base64.getEncoder().encodeToString(privateKey.getEncoded());
        } catch (Exception e) {
            log.warn("Failed when gen private key: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String generatePublicKey(String privateKey) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), BigInteger.valueOf(65537));
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid private key");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid algorithm");
        }
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getPrivateKeyFromKeyPair(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    @Override
    public String getPublicKeyFromKeyPair(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }


}

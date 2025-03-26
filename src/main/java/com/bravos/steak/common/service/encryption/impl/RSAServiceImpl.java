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
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class RSAServiceImpl implements RSAService {

    private static final String ALGORITHM = "RSA";
    private static final KeyFactory KEY_FACTORY;

    private static final ThreadLocal<KeyPairGenerator> KEY_PAIR_GENERATOR = ThreadLocal.withInitial(() -> {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(2048);
            return generator;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing KeyPairGenerator", e);
        }
    });

    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String plainText, String publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, convertPublicKey(publicKey));
            byte[] encryptedData = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String encryptedData, String privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, convertPrivateKey(privateKey));
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(encryptedDataBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSignatureData(String data, String privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(convertPrivateKey(privateKey));
            signature.update(data.getBytes());
            byte[] signDataBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signDataBytes);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyData(String data, String signatureData, String publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            byte[] signedDataBytes = Base64.getDecoder().decode(signatureData);
            signature.initVerify(convertPublicKey(publicKey));
            signature.update(data.getBytes());
            return signature.verify(signedDataBytes);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generatePublicKey(String privateKey) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KEY_FACTORY.generatePrivate(keySpec);
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), BigInteger.valueOf(65537));
            RSAPublicKey publicKey = (RSAPublicKey) KEY_FACTORY.generatePublic(publicKeySpec);
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid private key");
        }
    }

    @Override
    public KeyPair generateKeyPair() {
        return KEY_PAIR_GENERATOR.get().generateKeyPair();
    }

    @Override
    public String getPrivateKeyFromKeyPair(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    @Override
    public String getPublicKeyFromKeyPair(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    private PublicKey convertPublicKey(String publicKey) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return KEY_FACTORY.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid public key");
        }
    }

    private PrivateKey convertPrivateKey(String privateKey) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return KEY_FACTORY.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid private key");
        }
    }


}

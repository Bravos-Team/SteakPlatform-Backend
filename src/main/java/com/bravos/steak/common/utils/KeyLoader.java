package com.bravos.steak.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
public class KeyLoader {

    public static final PrivateKey PRIVATE_KEY = loadPrivateKey();
    public static final PublicKey PUBLIC_KEY = loadPublicKey();
    public static final SecretKey SECRET_KEY = loadSecretKey();

    public static PrivateKey loadPrivateKey() {
        final String path = "private.pem";
        try (FileReader fileReader = new FileReader(path)) {
            PEMParser pemParser = new PEMParser(fileReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();
            switch (object) {
                case PEMKeyPair pemKeyPair -> {
                    PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();
                    log.info("Private key loaded");
                    return converter.getPrivateKey(privateKeyInfo);
                }
                case PrivateKeyInfo privateKeyInfo -> {
                    log.info("Private key info loaded");
                    return converter.getPrivateKey(privateKeyInfo);
                }
                default -> throw new RuntimeException("Unknown object type: " + object.getClass().getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey loadPublicKey() {
        final String path = "public.pem";
        try (FileReader fileReader = new FileReader(path)) {
            PEMParser pemParser = new PEMParser(fileReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();
            return converter.getPublicKey(SubjectPublicKeyInfo.getInstance(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey loadSecretKey() {
        String secretKey = System.getProperty("SECRET_KEY");
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        if (decodedKey.length != 32) {
            throw new IllegalArgumentException("Khóa AES không hợp lệ (phải là 256-bit)");
        }
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

}

package com.bravos.steak.common.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class KeyLoader {

    public static final PrivateKey PRIVATE_KEY = loadPrivateKey();
    public static final PublicKey PUBLIC_KEY = loadPublicKey();
    public static final SecretKey SECRET_KEY = loadSecretKey();

    private static PrivateKey loadPrivateKey() {
        try {
            String key = new String(Files.readAllBytes(Path.of(Objects.requireNonNull(KeyLoader.class.getResource("private.pem")).toURI())));
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicKey loadPublicKey() {
        try {
            String key = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(KeyLoader.class.getResource("public.pem")).toURI())));
            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (IOException | URISyntaxException | NoSuchAlgorithmException | InvalidKeySpecException e) {
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

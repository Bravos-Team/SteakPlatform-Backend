package com.bravos.steak.common.service.encryption.impl;

import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.models.SignResult;
import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class KeyVaultServiceImpl implements KeyVaultService {

    private final SecretClient secretClient;
    private final CryptographyClient cryptographyClient;

    public KeyVaultServiceImpl(SecretClient secretClient, CryptographyClient cryptographyClient) {
        this.secretClient = secretClient;
        this.cryptographyClient = cryptographyClient;
    }

    @Override
    public String getSecretKey(String keyName) {
        try {
            KeyVaultSecret keyVaultSecret = secretClient.getSecret(keyName);
            return keyVaultSecret.getValue();
        } catch (Exception e) {
            log.error("Cannot get secretKey: {}",e.getMessage());
            throw new RuntimeException("Cannot get secret key");
        }
    }

    @Override
    public String signData(SignatureAlgorithm algorithm, byte[] data) {
        try {
            SignResult signResult = cryptographyClient.sign(algorithm,data);
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signResult.getSignature());
        } catch (Exception e) {
            log.error("Failed to sign data {}",e.getMessage(), e);
            throw new RuntimeException("Failed to sign data");
        }
    }

    @Override
    public PublicKey getPublicKey() {
        try {
            JsonWebKey jsonWebKey;
            try {
                KeyVaultKey keyVaultKey = cryptographyClient.getKey();
                jsonWebKey = keyVaultKey.getKey();
            } catch (Exception e) {
                log.error("Invalid key ", e);
                throw new RuntimeException("Invalid key");
            }
            BigInteger modulus = new BigInteger(1,jsonWebKey.getN());
            BigInteger exponent = new BigInteger(jsonWebKey.getE());
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus,exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid algorithm: {}", e.getMessage());
            throw new RuntimeException("Invalid algorithm");
        } catch (InvalidKeySpecException e) {
            log.error("Error when getting public key ", e);
            throw new RuntimeException("Error when getting public key");
        }
    }

}

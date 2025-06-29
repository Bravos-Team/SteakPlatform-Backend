package com.bravos.steak.common.configuration;

import com.bravos.steak.common.model.CdnKeyPair;
import com.bravos.steak.common.model.GeneralKeyPair;
import com.bravos.steak.common.model.SecretKeyX;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.bravos.steak.common.service.encryption.RSAService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.stream.Collectors;

@Configuration
public class KeyPairConfiguration {

    @Bean
    @Profile("dev")
    public GeneralKeyPair devGeneralKeyPair(RSAService rSAService) {
        PrivateKey privateKey = rSAService.convertPrivateKey(readPemFile("private-key.pem"));
        PublicKey publicKey = rSAService.convertPublicKey(readPemFile("public-key.pem"));
        return new GeneralKeyPair(privateKey,publicKey);
    }

    @Bean
    @Profile("prod")
    public GeneralKeyPair prodGeneralKeyPair(RSAService rSAService, KeyVaultService keyVaultService) {
        PrivateKey privateKey = rSAService.convertPrivateKey(keyVaultService.getSecretKey(System.getProperty("STEAK_PRIVATE_KEY")));
        PublicKey publicKey = rSAService.convertPublicKey(keyVaultService.getSecretKey(System.getProperty("STEAK_PUBLIC_KEY")));
        return new GeneralKeyPair(privateKey, publicKey);
    }

    @Bean
    @Profile("dev")
    public SecretKeyX devSecretKeyX() {
        String secretKey = System.getProperty("STEAK_SECRET_KEY");
        return new SecretKeyX(secretKey);
    }

    @Bean
    @Profile("prod")
    public SecretKeyX prodSecretKeyX(KeyVaultService keyVaultService) {
        String secretKey = keyVaultService.getSecretKey(System.getProperty("STEAK_SECRET_KEY"));
        return new SecretKeyX(secretKey);
    }

    @Bean
    @Profile("dev")
    public CdnKeyPair devCdnKeyPair(RSAService rSAService) {
        PrivateKey privateKey = rSAService.convertPrivateKey(readPemFile("cdn-private-key.pem"));
        PublicKey publicKey = rSAService.convertPublicKey(readPemFile("cdn-public-key.pem"));
        String cdnKeyPairId = System.getProperty("CDN_KEY_PAIR_ID");
        return new CdnKeyPair(privateKey, publicKey, cdnKeyPairId);
    }

    @Bean
    @Profile("prod")
    public CdnKeyPair prodCdnKeyPair(RSAService rSAService, KeyVaultService keyVaultService) {
        PrivateKey privateKey = rSAService.convertPrivateKey(keyVaultService.getSecretKey(System.getProperty("CDN_PRIVATE_KEY")));
        PublicKey publicKey = rSAService.convertPublicKey(keyVaultService.getSecretKey(System.getProperty("CDN_PUBLIC_KEY")));
        String cdnKeyPairId = keyVaultService.getSecretKey(System.getProperty("CDN_KEY_PAIR_ID"));
        return new CdnKeyPair(privateKey, publicKey, cdnKeyPairId);
    }

    private String readPemFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

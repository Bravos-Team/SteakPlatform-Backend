package com.bravos.steak.common.configuration;

import com.azure.core.credential.TokenCredential;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class KeyVaultConfiguration {

    @Bean
    public TokenCredential tokenCredential() {
        final String clientId = System.getenv("AZURE_CLIENT_ID");
        final String clientSecret = System.getenv("AZURE_CLIENT_SECRET");
        final String tenantId = System.getenv("AZURE_TENANT_ID");

        if(clientId == null || clientId.isBlank() ||
                clientSecret == null || clientSecret.isBlank() ||
                tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Key vault information cannot be null or empty");
        }

        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .retryTimeout(duration -> Duration.ofMinutes(3))
                .maxRetry(3)
                .build();
    }

    @Bean
    public SecretClient secretClient(TokenCredential tokenCredential) {
        final String vaultUrl = System.getenv("VAULT_URL");
        if(vaultUrl == null || vaultUrl.isBlank()) {
            throw new IllegalArgumentException("Key vault information cannot be null or empty");
        }
        return new SecretClientBuilder()
                .vaultUrl(vaultUrl)
                .credential(tokenCredential)
                .buildClient();
    }

}

package com.bravos.steak.common.configuration;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
public class KeyVaultConfiguration {

    @Bean
    @Profile("dev")
    public TokenCredential devTokenCredential() {
        return new ClientSecretCredentialBuilder()
                .clientId("123e4567-e89b-12d3-a456-426614174000") // Example client ID
                .clientSecret("your")
                .tenantId("123e4567-e89b-12d3-a456-426614174000") // Example tenant ID
                .build();
    }

    @Bean
    @Profile("prod")
    public TokenCredential prodTokenCredential() {
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
    @Profile("dev")
    public SecretClient devSecretClient(TokenCredential tokenCredential) {
        return new SecretClientBuilder()
                .vaultUrl("https://steak-dev-keyvault.vault.azure.net/")
                .credential(tokenCredential)
                .buildClient();
    }

    @Bean
    @Profile("prod")
    public SecretClient prodSecretClient(TokenCredential tokenCredential) {
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

package com.bravos.steak.common.configuration;

import com.bravos.steak.common.model.DiscordWebhookConfig;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DiscordWebhookConfiguration {

    @Bean
    @Profile("dev")
    public DiscordWebhookConfig devDiscordWebhookConfig() {
        return new DiscordWebhookConfig(System.getProperty("DISCORD_WEBHOOK_URL"));
    }

    @Bean
    @Profile("prod")
    public DiscordWebhookConfig prodDiscordWebhookConfig(KeyVaultService keyVaultService) {
        return new DiscordWebhookConfig(keyVaultService.getSecretKey(System.getProperty("DISCORD_WEBHOOK_URL")));
    }

    @Bean
    public WebClient discordWebhookClient(DiscordWebhookConfig discordWebhookConfig) {
        return WebClient.builder()
                .baseUrl(discordWebhookConfig.getWebhookUrl())
                .build();
    }

}

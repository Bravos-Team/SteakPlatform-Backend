package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OauthConfiguration {

    private final KeyVaultService keyVaultService;

    public OauthConfiguration(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Bean(name = "googleOauthService")
    public OAuth20Service googleOauthService() {
        String clientId = keyVaultService.getSecretKey(System.getProperty("GOOGLE_CLIENT_ID"));
        String apiSecret = keyVaultService.getSecretKey(System.getProperty("GOOGLE_API_SECRET"));
        String callbackUrl = keyVaultService.getSecretKey(System.getProperty("GOOGLE_CALLBACK_URL"));
        return new ServiceBuilder(clientId)
                .apiSecret(apiSecret)
                .callback(callbackUrl)
                .build(GoogleApi20.instance());

    }

    @Bean(name = "githubOauthService")
    public OAuth20Service gihubOauthService() {
        String clientId = keyVaultService.getSecretKey(System.getProperty("GITHUB_CLIENT_ID"));
        String apiSecret = keyVaultService.getSecretKey(System.getProperty("GITHUB_API_SECRET"));
        String callbackUrl = keyVaultService.getSecretKey(System.getProperty("GITHUB_CALLBACK_URL"));
        return new ServiceBuilder(clientId)
                .apiSecret(apiSecret)
                .callback(callbackUrl)
                .build(GitHubApi.instance());
    }

}

package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmailSenderConfiguration {

    private final KeyVaultService keyVaultService;

    public EmailSenderConfiguration(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Bean
    public WebClient emailSenderWebClient(@Value("${spring.profiles.active:dev}") String profile) {
        String emailApikey;
        String emailSecretKey;

        if(profile != null && profile.equals("prod")) {
            emailApikey = keyVaultService.getSecretKey(System.getProperty("EMAIL_API_KEY"));
            emailSecretKey = keyVaultService.getSecretKey(System.getProperty("EMAIL_SECRET_KEY"));
        } else {
            emailApikey = System.getProperty("EMAIL_API_KEY");
            emailSecretKey = System.getProperty("EMAIL_SECRET_KEY");
        }

        return WebClient.builder()
                .baseUrl("https://api.mailjet.com/v3.1/send")
                .defaultHeaders(headers-> {
                    headers.setBasicAuth(
                            emailApikey,
                            emailSecretKey);
                })
                .build();
    }

}

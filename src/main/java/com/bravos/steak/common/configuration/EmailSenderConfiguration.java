package com.bravos.steak.common.configuration;

import com.bravos.steak.common.model.EmailKeyPair;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EmailSenderConfiguration {

    @Bean
    @Profile("prod")
    public EmailKeyPair prodEmailKeyPair(KeyVaultService keyVaultService) {
        String emailApikey = keyVaultService.getSecretKey(System.getProperty("EMAIL_API_KEY"));
        String emailSecretKey = keyVaultService.getSecretKey(System.getProperty("EMAIL_SECRET_KEY"));
        return new EmailKeyPair(emailApikey, emailSecretKey);
    }

    @Bean
    @Profile("dev")
    public EmailKeyPair devEmailKeyPair() {
        String emailApikey = System.getProperty("EMAIL_API_KEY");
        String emailSecretKey = System.getProperty("EMAIL_SECRET_KEY");
        return new EmailKeyPair(emailApikey, emailSecretKey);
    }

    @Bean
    public WebClient emailSenderWebClient(EmailKeyPair emailKeyPair) {
        return WebClient.builder()
                .baseUrl("https://api.mailjet.com/v3.1/send")
                .defaultHeaders(headers -> headers.setBasicAuth(
                        emailKeyPair.getEmailApiKey(),
                        emailKeyPair.getEmailSecretKey()))
                .build();
    }

}

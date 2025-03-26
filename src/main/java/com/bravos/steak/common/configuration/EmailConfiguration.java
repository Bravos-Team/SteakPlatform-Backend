package com.bravos.steak.common.configuration;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfiguration {

    @Bean
    public MailjetClient mailClient() {
        return new MailjetClient(ClientOptions.builder()
                .baseUrl("https://api.mailjet.com/v3.1/send")
                .apiKey(System.getProperty("EMAIL_API_KEY"))
                .apiSecretKey(System.getProperty("EMAIL_SECRET_KEY"))
                .build());
    }

}

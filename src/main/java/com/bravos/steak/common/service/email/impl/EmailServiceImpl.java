package com.bravos.steak.common.service.email.impl;

import com.bravos.steak.common.service.email.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final WebClient webClient;
    private final String apiKeyPublic = System.getProperty("EMAIL_API_KEY");
    private final String apiKeyPrivate = System.getProperty("EMAIL_SECRET_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmailServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mailjet.com/v3.1/send")
                .defaultHeaders(headers -> headers.setBasicAuth(apiKeyPublic, apiKeyPrivate))
                .build();
    }

    public void sendEmailUsingTemplate(String to, String subject, String templateID, Map<String, Object> params) {
        try {

            String jsonBody = objectMapper.writeValueAsString(Map.of(
                    "Messages", new Object[]{
                            Map.of(
                                    "From", Map.of("Email", "steak@bravos.io.vn", "Name", "Steak"),
                                    "To", new Object[]{Map.of("Email", to)},
                                    "TemplateID", Integer.parseInt(templateID),
                                    "TemplateLanguage", true,
                                    "Subject", subject,
                                    "Variables", params
                            )
                    }
            ));

            webClient.post()
                    .header("Content-Type", "application/json")
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Email sent: {}", response))
                    .doOnError(error -> log.error("Failed to send email: {}", error.getMessage()))
                    .subscribe();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

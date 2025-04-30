package com.bravos.steak.common.service.email.impl;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final String apiKeyPublic = System.getProperty("EMAIL_API_KEY");

    private final String apiKeyPrivate = System.getProperty("EMAIL_SECRET_KEY");

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.mailjet.com/v3.1/send")
            .defaultHeaders(headers -> headers.setBasicAuth(apiKeyPublic, apiKeyPrivate))
            .build();

    @Override
    public void sendEmailUsingTemplate(EmailPayload emailPayload) {
        executorService.submit(() -> {
            this.sendEmail(emailPayload)
                    .doOnSuccess(response -> log.info("Email was sent to: {}", emailPayload.getTo()))
                    .doOnError(error -> log.error("Failed to send email: {}", error.getMessage()))
                    .retry(3)
                    .subscribe();
        });
    }

    private Mono<String> sendEmail(EmailPayload emailPayload) {
        Map<String, Object> jsonBody = Map.of(
                "Messages", new Object[]{
                        Map.of(
                                "From", Map.of("Email", emailPayload.getFrom(), "Name", "Steak"),
                                "To", new Object[]{Map.of("Email", emailPayload.getTo())},
                                "TemplateID", Integer.parseInt(emailPayload.getTemplateID()),
                                "TemplateLanguage", true,
                                "Subject", emailPayload.getSubject(),
                                "Variables", emailPayload.getParams()
                        )
                }
        );

        return webClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class);
    }

}

package com.bravos.steak.common.service.email.impl;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private final WebClient emailSenderWebClient;

    public EmailServiceImpl(WebClient emailSenderWebClient) {
        this.emailSenderWebClient = emailSenderWebClient;
    }

    @Override
    public void sendEmailUsingTemplate(EmailPayload emailPayload) {
        EXECUTOR_SERVICE.submit(() -> {
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

        return emailSenderWebClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class);
    }

}

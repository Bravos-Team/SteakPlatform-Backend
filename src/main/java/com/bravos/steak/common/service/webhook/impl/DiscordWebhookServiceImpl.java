package com.bravos.steak.common.service.webhook.impl;

import com.bravos.steak.common.model.DiscordErrorMessage;
import com.bravos.steak.common.service.webhook.DiscordWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Profile("prod")
public class DiscordWebhookServiceImpl implements DiscordWebhookService {

    private final WebClient discordWebhookClient;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public DiscordWebhookServiceImpl(WebClient discordWebhookClient) {
        this.discordWebhookClient = discordWebhookClient;
    }

    @Override
    public void sendError(String message, Throwable ex) {
        executorService.submit(() -> {
            try {
                handleError(message, ex);
            } catch (Exception e) {
                log.error("Failed to send error message to Discord", e);
            }
        });
    }

    private void handleError(String message, Throwable ex) {
        if(message == null || message.isEmpty()) {
            message = "Error occurred in the application";
        }
        DiscordErrorMessage error = new DiscordErrorMessage(
                "Application Exception",
                message,
                ex != null ? ex.toString() : null,
                ZonedDateTime.now(ZoneId.of("GMT+7")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
        this.handleSendEmbed(error);
    }

    private void handleSendEmbed(DiscordErrorMessage error) {
        Map<String, Object> embed = Map.of(
                "title", error.getTitle(),
                "fields", List.of(
                        Map.of("name", "Details", "value",
                                error.getDetails(), "inline", false),
                        Map.of("name", "Exception", "value",
                                error.getException() != null ? error.getException() : "N/A", "inline", false),
                        Map.of("name", "Timestamp", "value",
                                error.getTimestamp(), "inline", false)
                ),
                "color", 15158332
        );
        Map<String, Object> payload = Map.of(
                "embeds", List.of(embed)
        );
        discordWebhookClient.post()
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(3)
                .subscribe();
    }

}
package com.bravos.steak.common.service.email.impl;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.webhook.DiscordWebhookService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final WebClient emailSenderWebClient;
    private static final int MAX_RETRIES = 3;

    private final Deque<EmailPayload> emailPayloads = new ArrayDeque<>(50);
    private final DiscordWebhookService discordWebhookService;

    public EmailServiceImpl(WebClient emailSenderWebClient, DiscordWebhookService discordWebhookService) {
        this.emailSenderWebClient = emailSenderWebClient;
        this.discordWebhookService = discordWebhookService;
    }

    @Override
    public void sendEmail(EmailPayload emailPayload) {
        this.emailPayloads.add(emailPayload);
    }

    @Scheduled(fixedDelay = 5000, timeUnit = TimeUnit.MILLISECONDS)
    protected void handleBatching() {
        if (!emailPayloads.isEmpty()) {
            if (emailPayloads.size() == 1) {
                EmailPayload emailPayload = emailPayloads.removeFirst();
                if (emailPayload != null && !emailPayload.getTemplateID().isBlank()) {
                    this.sendSingleEmail(emailPayload).subscribe();
                }
            } else {
                List<EmailPayload> payloadsToSend = new ArrayList<>(50);
                while (!emailPayloads.isEmpty() && payloadsToSend.size() < 50) {
                    EmailPayload emailPayload = emailPayloads.removeFirst();
                    if (emailPayload != null && !emailPayload.getTemplateID().isBlank()) {
                        payloadsToSend.add(emailPayload);
                    }
                }
                this.sendBatchEmails(payloadsToSend).subscribe();
            }
        }
    }

    private Mono<String> sendSingleEmail(EmailPayload emailPayload) {
        RequestBody requestBody;
        if(emailPayload.getTemplateID() != null && !emailPayload.getTemplateID().isBlank()) {
            Message message = new TemplateMessage(
                    new From(emailPayload.getFrom(), "Steak"),
                    List.of(new To(emailPayload.getTo())),
                    emailPayload.getSubject(),
                    Integer.parseInt(emailPayload.getTemplateID()),
                    true,
                    emailPayload.getParams()
            );
            requestBody = new RequestBody(List.of(message));
        } else if(emailPayload.getHtmlPart() != null && !emailPayload.getHtmlPart().isBlank()) {
            Message message = new HTMLMessage(
                    new From(emailPayload.getFrom(), "Steak"),
                    List.of(new To(emailPayload.getTo())),
                    emailPayload.getSubject(),
                    emailPayload.getHtmlPart()
            );
            requestBody = new RequestBody(List.of(message));
        } else {
            log.warn("Email payload does not have a valid template ID or HTML part, skipping email send.");
            return Mono.empty();
        }

        return emailSenderWebClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Email was sent to: {}", emailPayload.getTo()))
                .retryWhen(Retry.fixedDelay(MAX_RETRIES, Duration.ofSeconds(1))
                        .doAfterRetry(retrySignal -> {
                            if(retrySignal.totalRetries() + 1 == MAX_RETRIES) {
                                log.error("Failed to send email after {} attempts", MAX_RETRIES);
                                discordWebhookService.sendError("Failed to send batch email", retrySignal.failure());
                            }
                        })
                );
    }

    private Mono<Object> sendBatchEmails(List<EmailPayload> payloads) {
        List<Message> messages = new ArrayList<>(50);

        for (EmailPayload payload : payloads) {
            if(payload != null) {
                if(payload.getTemplateID() != null && !payload.getTemplateID().isBlank()) {
                    messages.add(new TemplateMessage(
                            new From(payload.getFrom(), "Steak"),
                            List.of(new To(payload.getTo())),
                            payload.getSubject(),
                            Integer.parseInt(payload.getTemplateID()),
                            true,
                            payload.getParams()

                    ));
                } else if(payload.getHtmlPart() != null && !payload.getHtmlPart().isBlank()) {
                    messages.add(new HTMLMessage(
                            new From(payload.getFrom(), "Steak"),
                            List.of(new To(payload.getTo())),
                            payload.getSubject(),
                            payload.getHtmlPart()
                    ));
                }
            }
        }

        RequestBody requestBody = new RequestBody(messages);

        return emailSenderWebClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.info("Batch email sent to: {}",
                        payloads.stream().map(EmailPayload::getTo).toList()))
                .retryWhen(Retry.fixedDelay(MAX_RETRIES, Duration.ofSeconds(1))
                        .doAfterRetry(retrySignal -> {
                            if(retrySignal.totalRetries() + 1 == MAX_RETRIES) {
                                log.error("Failed to send batch email after {} attempts", MAX_RETRIES);
                                discordWebhookService.sendError("Failed to send batch email", retrySignal.failure());
                            }
                        })
                );
    }

    private record RequestBody(List<Message> messages) {
    }

    @Getter
    @Setter
    private static class Message {
        private final From From;
        private final List<To> To;
        private final String Subject;

        private Message(From from, List<To> tos, String subject) {
            From = from;
            To = tos;
            Subject = subject;
        }
    }

    @Getter
    @Setter
    private static class TemplateMessage extends Message {
        private final Integer TemplateID;
        private final boolean TemplateLanguage;
        private final Map<String, Object> Variables;

        private TemplateMessage(From from, List<To> tos, String subject,
                                Integer templateID, boolean templateLanguage,
                                Map<String, Object> variables) {
            super(from, tos, subject);
            this.TemplateID = templateID;
            this.TemplateLanguage = templateLanguage;
            this.Variables = variables;
        }
    }

    @Getter
    @Setter
    private static class HTMLMessage extends Message {
        private final String HTMLPart;

        private HTMLMessage(From from, List<To> tos, String subject, String htmlPart) {
            super(from, tos, subject);
            this.HTMLPart = htmlPart;
        }
    }

    @Getter
    @Setter
    private static class From {
        private final String Email;
        private final String Name;

        public From(String email, String name) {
            this.Email = email;
            this.Name = name;
        }
    }

    @Getter
    public static class To {
        private final String Email;

        public To(String email) {
            this.Email = email;
        }
    }

}

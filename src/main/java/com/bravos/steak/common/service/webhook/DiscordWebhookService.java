package com.bravos.steak.common.service.webhook;

public interface DiscordWebhookService {

    void sendError(String message, Throwable ex);

}

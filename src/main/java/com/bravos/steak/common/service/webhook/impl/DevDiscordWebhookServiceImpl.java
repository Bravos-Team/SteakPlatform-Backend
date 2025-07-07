package com.bravos.steak.common.service.webhook.impl;

import com.bravos.steak.common.service.webhook.DiscordWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev")
public class DevDiscordWebhookServiceImpl implements DiscordWebhookService {

    @Override
    public void sendError(String message, Throwable ex) {
        log.warn("Dev profile active, not sending error to Discord: {}", message, ex);
        // do nothing in dev profile
    }

}

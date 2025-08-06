package com.bravos.steak.socket.service.impl;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.socket.model.GameTrackingMessage;
import com.bravos.steak.socket.model.PlayGameSession;
import com.bravos.steak.socket.service.TrackingUserGameService;
import com.bravos.steak.store.model.event.CounterEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TrackingUserGameServiceImpl implements TrackingUserGameService {

    private final RedisService redisService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TrackingUserGameServiceImpl(RedisService redisService,
                                       ApplicationEventPublisher applicationEventPublisher) {
        this.redisService = redisService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void trackUserGameAction(GameTrackingMessage message) {
        switch (message.getAction()) {
            case GameTrackingMessage.START -> startGameSession(message);
            case GameTrackingMessage.STOP -> stopGameSession(message);
            default -> throw new BadRequestException("Invalid action for game tracking");
        }
    }

    @Override
    public void startGameSession(GameTrackingMessage message) {
        long userId = message.getUserId();
        String trackingUserKey = "tracking:game:" + userId + ":" + message.getGameId();
        PlayGameSession session = redisService.get(trackingUserKey, PlayGameSession.class);
        long currentTime = DateTimeHelper.currentTimeMillis();
        if(session != null) {
            if (!session.getDeviceId().equals(message.getDeviceId())) {
                throw new ForbiddenException("You are playing this game on another device");
            }
            session.setLatestPingTime(currentTime);
        } else {
            applicationEventPublisher.publishEvent(new CounterEvent.IncreasePlayingCountEvent(
                    this, message.getGameId(), userId));
            session = PlayGameSession.builder()
                    .deviceId(message.getDeviceId())
                    .startTime(currentTime)
                    .latestPingTime(currentTime)
                    .build();
        }
        redisService.save(trackingUserKey, session, 10, TimeUnit.MINUTES);
    }

    @Override
    public void stopGameSession(GameTrackingMessage message) {
        long userId = message.getUserId();
        String trackingUserKey = "tracking:game:" + userId + ":" + message.getGameId();
        PlayGameSession session = redisService.get(trackingUserKey, PlayGameSession.class);
        if (session != null && !session.getDeviceId().equals(message.getDeviceId())) {
            throw new ForbiddenException("You are playing this game on another device");
        } else if(session != null) {
            applicationEventPublisher.publishEvent(new CounterEvent.DecreasePlayingCountEvent(
                    this, message.getGameId(), userId,
                    session.getLatestPingTime() - session.getStartTime()));
        }
        redisService.delete(trackingUserKey);
    }

}

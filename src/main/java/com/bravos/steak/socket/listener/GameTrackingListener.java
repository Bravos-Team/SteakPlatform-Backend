package com.bravos.steak.socket.listener;

import com.bravos.steak.socket.model.GameTrackingMessage;
import com.bravos.steak.socket.service.TrackingUserGameService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class GameTrackingListener {

    private final TrackingUserGameService trackingUserGameService;

    public GameTrackingListener(TrackingUserGameService trackingUserGameService) {
        this.trackingUserGameService = trackingUserGameService;
    }

    @EventListener
    public void onGameStart(SessionConnectedEvent sessionConnectedEvent) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(sessionConnectedEvent.getMessage());
        handleTracking(headerAccessor, GameTrackingMessage.START);
    }

    @EventListener
    public void onGameStop(SessionConnectedEvent sessionConnectedEvent) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(sessionConnectedEvent.getMessage());
        handleTracking(headerAccessor, GameTrackingMessage.STOP);
    }

    private void handleTracking(SimpMessageHeaderAccessor headerAccessor, String action) {
        if(headerAccessor.getSessionAttributes() == null) {
            return;
        }
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        Long gameId = (Long) headerAccessor.getSessionAttributes().get("gameId");
        String deviceId = (String) headerAccessor.getSessionAttributes().get("deviceId");

        GameTrackingMessage message = GameTrackingMessage.builder()
                .userId(userId)
                .gameId(gameId)
                .action(action)
                .deviceId(deviceId)
                .build();

        trackingUserGameService.trackUserGameAction(message);
    }

}

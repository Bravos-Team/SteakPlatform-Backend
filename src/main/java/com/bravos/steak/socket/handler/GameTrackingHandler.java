package com.bravos.steak.socket.handler;

import com.bravos.steak.socket.model.GameTrackingMessage;
import com.bravos.steak.socket.service.TrackingUserGameService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class GameTrackingHandler extends TextWebSocketHandler {

    private final TrackingUserGameService trackingUserGameService;

    public GameTrackingHandler(TrackingUserGameService trackingUserGameService) {
        this.trackingUserGameService = trackingUserGameService;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,
                                     @NonNull TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("pong"));
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        handleTracking(session, GameTrackingMessage.START);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
                                      @NonNull CloseStatus status) {
        handleTracking(session, GameTrackingMessage.STOP);
    }

    private void handleTracking(WebSocketSession session, String action) {
        Long userId = (Long) session.getAttributes().get("userId");
        Long gameId = (Long) session.getAttributes().get("gameId");
        String deviceId = String.valueOf(session.getAttributes().get("deviceId"));

        GameTrackingMessage message = GameTrackingMessage.builder()
                .userId(userId)
                .gameId(gameId)
                .action(action)
                .deviceId(deviceId)
                .build();

        trackingUserGameService.trackUserGameAction(message);
        log.info("Game tracking action: {} for userId: {}, gameId: {}, deviceId: {}",
                action, userId, gameId, deviceId);
    }

}

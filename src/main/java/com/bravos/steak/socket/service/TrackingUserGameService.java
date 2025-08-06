package com.bravos.steak.socket.service;

import com.bravos.steak.socket.model.GameTrackingMessage;
import org.springframework.stereotype.Service;

@Service
public interface TrackingUserGameService {

    void trackUserGameAction(GameTrackingMessage message);

    void startGameSession(GameTrackingMessage message);

    void stopGameSession(GameTrackingMessage message);

}

package com.bravos.steak.socket.configuration;

import com.bravos.steak.socket.handler.GameTrackingHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class SocketConfiguration implements WebSocketConfigurer {

    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final GameTrackingHandler gameTrackingHandler;

    public SocketConfiguration(AuthHandshakeInterceptor authHandshakeInterceptor, GameTrackingHandler gameTrackingHandler) {
        this.authHandshakeInterceptor = authHandshakeInterceptor;
        this.gameTrackingHandler = gameTrackingHandler;
    }


    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(gameTrackingHandler, "/ws/tracking")
                .setAllowedOrigins("*")
                .addInterceptors(authHandshakeInterceptor);
    }



}

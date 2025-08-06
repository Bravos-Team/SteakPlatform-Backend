package com.bravos.steak.socket.configuration;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public AuthHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {

        String token = getTokenFromRequest(request);
        if(token == null) return false;

        long gameId;
        try {
            gameId = Long.parseLong(getGameIdFromRequest(request));
        } catch (NumberFormatException e) {
            return false;
        }

        String deviceId = getDeviceIdFromRequest(request);
        if(deviceId == null || deviceId.isBlank()) {
            return false;
        }

        JwtTokenClaims claims;
        try {
            claims = jwtService.getClaims(token);
        } catch (Exception e) {
            return false;
        }
        if(claims == null) return false;

        attributes.put("userId", claims.getId());
        attributes.put("gameId", gameId);
        attributes.put("deviceId", deviceId);

        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {

        if(exception != null) {
            log.error("WebSocket handshake failed", exception);
        }

    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("Authorization");
        if(token == null || token.isBlank()) {
            return null;
        }
        return token.replace("Bearer ", "");
    }

    private String getGameIdFromRequest(ServerHttpRequest request) {
        String gameId = request.getHeaders().getFirst("Game-Id");
        if(gameId == null || gameId.isBlank()) {
            return "";
        }
        return gameId;
    }

    private String getDeviceIdFromRequest(ServerHttpRequest request) {
        String deviceId = request.getHeaders().getFirst("Device-Id");
        if(deviceId == null || deviceId.isBlank()) {
            return null;
        }
        return deviceId;
    }

}

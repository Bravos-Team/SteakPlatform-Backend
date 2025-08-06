package com.bravos.steak.socket.service.impl;

import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.socket.service.SocketService;
import org.springframework.stereotype.Service;

@Service
public class SocketServiceImpl implements SocketService {

    private final SessionService sessionService;

    public SocketServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String getSocketToken() {
        return sessionService.getAuthentication().getCredentials().toString();
    }

}


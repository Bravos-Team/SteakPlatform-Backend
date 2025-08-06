package com.bravos.steak.socket.controller;

import com.bravos.steak.socket.service.SocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/socket")
public class SocketController {

    private final SocketService socketService;

    public SocketController(SocketService socketService) {
        this.socketService = socketService;
    }

    @GetMapping("/token")
    public ResponseEntity<?> getSocketToken() {
        return ResponseEntity.ok(socketService.getSocketToken());
    }

}

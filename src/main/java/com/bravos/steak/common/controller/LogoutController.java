package com.bravos.steak.common.controller;

import com.bravos.steak.common.service.auth.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logout")
public class LogoutController {

    private final SessionService sessionService;

    @Autowired
    public LogoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{role}")
    public ResponseEntity<?> logout(@PathVariable String role) {
        if(!role.equalsIgnoreCase("user") &&
                !role.equalsIgnoreCase("admin") &&
                !role.equalsIgnoreCase("publisher")) {
            return ResponseEntity.noContent().build();
        }
        sessionService.logout(role);
        return ResponseEntity.ok().build();
    }

}

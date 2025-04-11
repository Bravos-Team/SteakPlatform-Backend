package com.bravos.steak.common.controller;

import com.bravos.steak.common.service.auth.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logout")
public class LogoutController {

    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @GetMapping
    public ResponseEntity<?> logout() {
        logoutService.logout();
        return ResponseEntity.ok("Logout successfully");
    }

}

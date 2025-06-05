package com.bravos.steak.administration.controller;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(@Qualifier("adminAuthService") AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        Account account = authService.login(usernameLoginRequest);
        return ResponseEntity.ok(Map.of("username",account.getUsername()));
    }

    @PostMapping("/email-login")
    public ResponseEntity<?> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        Account account = authService.login(emailLoginRequest);
        return ResponseEntity.ok(Map.of("username",account.getUsername()));
    }

}

package com.bravos.steak.dev.controller;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.dev.model.request.PublisherRegistrationRequest;
import com.bravos.steak.dev.service.PublisherRegistrationService;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dev/auth")
public class PublisherAuthController {

    private final PublisherRegistrationService publisherRegistrationService;

    private final AuthService authService;

    @Autowired
    public PublisherAuthController(PublisherRegistrationService publisherRegistrationService,
                                   @Qualifier("publisherAuthService") AuthService authService) {
        this.publisherRegistrationService = publisherRegistrationService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid PublisherRegistrationRequest publisherRegistrationRequest) {
        publisherRegistrationService.preRegisterPublisher(publisherRegistrationRequest);
        return ResponseEntity.ok().build();
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

    @PostMapping("/refresh")
    public ResponseEntity<?> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        Account account = authService.renewToken(refreshRequest);
        return ResponseEntity.ok().body(Map.of(
                "username", account.getUsername()
        ));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

}

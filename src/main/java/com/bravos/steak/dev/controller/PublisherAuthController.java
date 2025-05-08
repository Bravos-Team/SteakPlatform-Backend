package com.bravos.steak.dev.controller;

import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.model.request.PublisherRegistrationRequest;
import com.bravos.steak.dev.service.PublisherRegistrationService;
import com.bravos.steak.dev.service.impl.PublisherAuthService;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dev/auth")
@RequiredArgsConstructor
public class PublisherAuthController {

    private final PublisherRegistrationService publisherRegistrationService;
    private final PublisherAuthService publisherAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid PublisherRegistrationRequest publisherRegistrationRequest) {
        publisherRegistrationService.preRegisterPublisher(publisherRegistrationRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-username")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        PublisherAccount account = (PublisherAccount) publisherAuthService.login(usernameLoginRequest);
        return ResponseEntity.ok(Map.of("username",account.getUsername()));
    }

}

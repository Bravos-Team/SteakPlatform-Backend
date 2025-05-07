package com.bravos.steak.dev.controller;

import com.bravos.steak.dev.model.request.PublisherRegistrationRequest;
import com.bravos.steak.dev.service.PublisherRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/auth")
@RequiredArgsConstructor
public class PublisherAuthController {

    private final PublisherRegistrationService publisherRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid PublisherRegistrationRequest publisherRegistrationRequest) {
        publisherRegistrationService.preRegisterPublisher(publisherRegistrationRequest);
        return ResponseEntity.ok().build();
    }

}

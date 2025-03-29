package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.account.service.RegistrationService;
import com.bravos.steak.exceptions.AccountAlreadyExistsException;
import com.bravos.steak.exceptions.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/register")
public class RegisterController {

    private final RegistrationService registrationService;

    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            return ResponseEntity.ok(registrationService.preRegisterAccount(registrationRequest));
        } catch (AccountAlreadyExistsException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}

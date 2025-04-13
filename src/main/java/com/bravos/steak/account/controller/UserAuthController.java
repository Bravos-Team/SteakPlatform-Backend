package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.request.EmailLoginRequest;
import com.bravos.steak.account.model.request.RefreshRequest;
import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.account.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account/auth")
public class UserAuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(registrationService.preRegisterAccount(registrationRequest));
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        LoginResponse loginResponse = authService.login(usernameLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/email-login")
    public ResponseEntity<?> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        LoginResponse loginResponse = authService.login(emailLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        LoginResponse loginResponse = authService.renewToken(refreshRequest);
        return ResponseEntity.ok(loginResponse);
    }
    
}

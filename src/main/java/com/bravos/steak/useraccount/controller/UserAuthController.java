package com.bravos.steak.useraccount.controller;

import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.RegistrationRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import com.bravos.steak.useraccount.model.response.LoginResponse;
import com.bravos.steak.useraccount.service.RegistrationService;
import com.bravos.steak.useraccount.service.impl.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(registrationService.preRegisterAccount(registrationRequest));
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        LoginResponse loginResponse = userAuthService.login(usernameLoginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/email-login")
    public ResponseEntity<?> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        LoginResponse loginResponse = userAuthService.login(emailLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        LoginResponse loginResponse = userAuthService.renewToken(refreshRequest);
        return ResponseEntity.ok(loginResponse);
    }
    
}

package com.bravos.steak.useraccount.controller;

import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UserRegistrationRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import com.bravos.steak.useraccount.model.response.UserLoginResponse;
import com.bravos.steak.useraccount.service.UserRegistrationService;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.impl.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final UserRegistrationService userRegistrationService;
    private final UserAccountService userAccountService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        userRegistrationService.preRegisterAccount(userRegistrationRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        Long userId = userAuthService.login(usernameLoginRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(userId);
        return ResponseEntity.ok().body(userLoginResponse);
    }

    @PostMapping("/email-login")
    public ResponseEntity<?> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        Long userId = userAuthService.login(emailLoginRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(userId);
        return ResponseEntity.ok(userLoginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        Long userId = userAuthService.renewToken(refreshRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(userId);
        return ResponseEntity.ok(userLoginResponse);
    }
    
}

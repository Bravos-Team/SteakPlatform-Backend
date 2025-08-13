package com.bravos.steak.administration.controller;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(@Qualifier("adminAuthService") AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/username-login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        Account account = authService.login(usernameLoginRequest);
        return ResponseEntity.ok(new LoginResponse(account.getUsername()));
    }

    @PostMapping("/email-login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        Account account = authService.login(emailLoginRequest);
        return ResponseEntity.ok(new LoginResponse(account.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        Account account = authService.renewToken(refreshRequest);
        return ResponseEntity.ok(new LoginResponse(account.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class LoginResponse {
        private String username;

        public LoginResponse(String username) {
            this.username = username;
        }

    }

}

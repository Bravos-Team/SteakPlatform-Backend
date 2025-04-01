package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.AuthService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/user")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        LoginResponse loginResponse = authService.login(usernameLoginRequest);
        if(loginResponse == null){
            throw new ResourceNotFoundException("Invalid username or password");
        }

        return ResponseEntity.ok(loginResponse);
    }



}

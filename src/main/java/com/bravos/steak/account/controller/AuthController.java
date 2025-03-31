package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SnowflakeGenerator generalIdGenerator;


    public AuthController(AccountService accountService, PasswordEncoder passwordEncoder, JwtService jwtService, SnowflakeGenerator generalIdGenerator) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.generalIdGenerator = generalIdGenerator;
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {



        return null;
    }



}

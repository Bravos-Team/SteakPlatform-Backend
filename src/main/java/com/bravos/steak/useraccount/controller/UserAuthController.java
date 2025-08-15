package com.bravos.steak.useraccount.controller;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.useraccount.model.request.*;
import com.bravos.steak.useraccount.model.response.UserLoginResponse;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {

    private final UserRegistrationService userRegistrationService;
    private final UserAccountService userAccountService;

    private final AuthService authService;

    @Autowired
    public UserAuthController(UserRegistrationService userRegistrationService, UserAccountService userAccountService,
                              @Qualifier("userAuthService") AuthService authService) {
        this.userRegistrationService = userRegistrationService;
        this.userAccountService = userAccountService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        userRegistrationService.preRegisterAccount(userRegistrationRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/username-login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernameLoginRequest usernameLoginRequest) {
        Account userAccount = authService.login(usernameLoginRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(userAccount.getId());
        return ResponseEntity.ok().body(userLoginResponse);
    }

    @GetMapping("/oauth2-state")
    public ResponseEntity<?> getOauth2State(@RequestParam String deviceId) {
        String state = authService.generateOAuth2LoginState(deviceId);
        return ResponseEntity.ok(state);
    }

    @PostMapping("/oauth2-login")
    public ResponseEntity<?> oauthLogin(@RequestBody @Valid OauthLoginRequest oauthLoginRequest) {
        Account account = authService.oauthLogin(oauthLoginRequest);
//        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(account.getId());
//        return ResponseEntity.ok(userLoginResponse);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-login")
    public ResponseEntity<?> login(@RequestBody @Valid EmailLoginRequest emailLoginRequest) {
        Account account = authService.login(emailLoginRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(account.getId());
        return ResponseEntity.ok(userLoginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> renewToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        Account account = authService.renewToken(refreshRequest);
        UserLoginResponse userLoginResponse = userAccountService.getLoginResponseById(account.getId());
        return ResponseEntity.ok(userLoginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }
    
}

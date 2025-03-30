package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.model.enums.AccountStatus;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.AuthService;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.UnauthorizeException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeGenerator generalIdGenerator;
    private final JwtService jwtService;

    public AuthServiceImpl(AccountService accountService, PasswordEncoder passwordEncoder, SnowflakeGenerator generalIdGenerator, JwtService jwtService) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.generalIdGenerator = generalIdGenerator;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {
        Account account = accountService.getAccountByUsername(usernameLoginRequest.getUsername());

        if(account == null) {
            throw new UnauthorizeException("Username or password is invalid");
        }
        if(account.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }

        boolean isCorrectPassword = passwordEncoder.matches(usernameLoginRequest.getPassword(),account.getPassword());

        if(!isCorrectPassword) {
            throw new UnauthorizeException("Username or password is invalid");
        }

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .build();

        long refreshToken = generalIdGenerator.generateId();

        String token = jwtService.generateToken(jwtTokenClaims);

        return null;
    }

    @Override
    public String renewToken(String refreshToken) {
        return "";
    }

}

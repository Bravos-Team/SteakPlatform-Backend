package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.model.enums.AccountStatus;
import com.bravos.steak.account.model.mappers.AccountMapper;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.AuthService;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.utils.JwtUtils;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.UnauthorizeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeGenerator generalIdGenerator;
    private final JwtService jwtService;
    private final AccountMapper accountMapper;

    @Override
    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                usernameLoginRequest.getUsername(),usernameLoginRequest.getPassword()
        ));

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

        long refreshToken = generalIdGenerator.generateId();

        String token = JwtUtils.generateToken(account);

        return LoginResponse.builder()
                .accountDTO(accountMapper.toAccountDTO(account))
                .refreshToken("in feature")
                .accessToken(token)
                .build();
    }

    @Override
    public String renewToken(String refreshToken) {
        return "";
    }

}

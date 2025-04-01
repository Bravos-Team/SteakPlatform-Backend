package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.entity.AccountRefreshToken;
import com.bravos.steak.account.model.enums.AccountStatus;
import com.bravos.steak.account.model.mappers.AccountMapper;
import com.bravos.steak.account.model.request.RefreshRequest;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.account.repo.AccountRefreshTokenRepository;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.AuthService;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.UnauthorizeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeGenerator generalIdGenerator;
    private final JwtService jwtService;
    private final AccountMapper accountMapper;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Override
    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {

        Account account = accountService.getAccountByUsername(usernameLoginRequest.getUsername());

        if(account == null || !passwordEncoder.matches(usernameLoginRequest.getPassword(), account.getPassword())) {
            throw new UnauthorizeException("Username or password is invalid");
        }

        if(account.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }

        String tokenRefresh = UUID.randomUUID().toString();
        long refreshTokenId = generalIdGenerator.generateId();
        LocalDateTime now = LocalDateTime.now();

        AccountRefreshToken accountRefreshToken = AccountRefreshToken.builder()
                .id(refreshTokenId)
                .account(account)
                .deviceId(usernameLoginRequest.getDeviceId())
                .issuesAt(Timestamp.valueOf(now))
                .expiresAt(Timestamp.valueOf(now.plusDays(90)))
                .token(tokenRefresh)
                .build();

        accountRefreshTokenRepository.save(accountRefreshToken);

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .deviceId(usernameLoginRequest.getDeviceId())
                .jti(refreshTokenId)
                .permissions(List.of())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusMinutes(30).toEpochSecond(ZoneOffset.UTC))
                .roles(account.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();

        String token = jwtService.generateToken(jwtTokenClaims);

        return LoginResponse.builder()
                .accountDTO(accountMapper.toAccountDTO(account))
                .refreshToken(tokenRefresh)
                .accessToken(token)
                .build();
    }

    @Override
    public String renewToken(RefreshRequest refreshRequest) {



        return "";
    }

}

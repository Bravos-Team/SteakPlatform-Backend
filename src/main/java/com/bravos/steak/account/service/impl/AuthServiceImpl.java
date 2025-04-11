package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.entity.AccountRefreshToken;
import com.bravos.steak.account.model.enums.AccountStatus;
import com.bravos.steak.account.model.request.EmailLoginRequest;
import com.bravos.steak.account.model.request.RefreshRequest;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.account.repo.AccountRefreshTokenRepository;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.AuthService;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.UnauthorizeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeGenerator generalIdGenerator;
    private final JwtService jwtService;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;
    private final RedisService redisService;

    @Override
    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {
        Account account = accountService.getAccountByUsername(usernameLoginRequest.getUsername());

        String rawPassword = usernameLoginRequest.getPassword();
        String deviceId = usernameLoginRequest.getDeviceId();

        this.validateAccount(account, rawPassword);

        return generateTokenPair(account,deviceId);
    }

    @Override
    public LoginResponse login(EmailLoginRequest emailLoginRequest) {
        Account account = accountService.getAccountByEmail(emailLoginRequest.getEmail());

        String rawPassword = emailLoginRequest.getPassword();
        String deviceId = emailLoginRequest.getDeviceId();

        this.validateAccount(account, rawPassword);

        return generateTokenPair(account,deviceId);
    }

    @Override
    public LoginResponse renewToken(RefreshRequest refreshRequest) {

        String refreshToken = refreshRequest.getRefreshToken();
        String deviceId = refreshRequest.getDeviceId();

        AccountRefreshToken accountRefreshToken = accountRefreshTokenRepository.findByTokenAndDeviceId(refreshToken, deviceId)
                .orElseThrow(() -> new UnauthorizeException("Invalid refresh token"));

        Account account = accountRefreshToken.getAccount();

        this.validateRefreshToken(account, accountRefreshToken);

        LocalDateTime now = LocalDateTime.now();

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .deviceId(deviceId)
                .jti(accountRefreshToken.getId())
                .permissions(List.of())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusMinutes(30).toEpochSecond(ZoneOffset.UTC))
                .roles(account.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();

        String token = jwtService.generateToken(jwtTokenClaims);

        return new LoginResponse(token, refreshToken);

    }

    private void validateAccount(Account account, String rawPassword) {
        if(account == null || !passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new UnauthorizeException("Username or password is invalid");
        }

        if(account.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }
    }

    private void validateRefreshToken(Account account, AccountRefreshToken accountRefreshToken) {
        if(account.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }

        if(accountRefreshToken.getRevoked()) {
            throw new UnauthorizeException("Refresh token is revoked");
        }

        if(accountRefreshToken.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new UnauthorizeException("Refresh token is expired");
        }

        if(accountRefreshToken.getIssuesAt().before(Timestamp.valueOf(account.getUpdatedTime()))) {
            throw new UnauthorizeException("Refresh token cannot be used");
        }
    }

    private LoginResponse generateTokenPair(Account account, String deviceId) {

        String tokenRefresh = UUID.randomUUID().toString();
        long refreshTokenId = generalIdGenerator.generateId();
        LocalDateTime now = LocalDateTime.now();

        AccountRefreshToken accountRefreshToken = AccountRefreshToken.builder()
                .id(refreshTokenId)
                .account(account)
                .deviceId(deviceId)
                .issuesAt(Timestamp.valueOf(now))
                .expiresAt(Timestamp.valueOf(now.plusDays(90)))
                .token(tokenRefresh)
                .build();

        accountRefreshTokenRepository.save(accountRefreshToken);

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .deviceId(deviceId)
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
                .refreshToken(tokenRefresh)
                .accessToken(token)
                .build();
    }

}

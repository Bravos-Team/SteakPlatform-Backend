package com.bravos.steak.common.service.auth;

import com.bravos.steak.account.model.enums.AccountStatus;
import com.bravos.steak.account.model.request.EmailLoginRequest;
import com.bravos.steak.account.model.request.RefreshRequest;
import com.bravos.steak.account.model.request.UsernameLoginRequest;
import com.bravos.steak.account.model.response.LoginResponse;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.model.AccountInfo;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.TooManyRequestException;
import com.bravos.steak.exceptions.UnauthorizeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public abstract class AuthService {

    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {
        this.checkLoginAttemps(usernameLoginRequest.getDeviceId());

        AccountInfo accountInfo = this.getAccountByUsername(usernameLoginRequest.getUsername());

        String rawPassword = usernameLoginRequest.getPassword();
        String deviceId = usernameLoginRequest.getDeviceId();

        this.validateAccount(accountInfo, rawPassword, deviceId);

        return getLoginResponse(accountInfo,deviceId);
    }

    public LoginResponse login(EmailLoginRequest emailLoginRequest) {
        this.checkLoginAttemps(emailLoginRequest.getDeviceId());

        AccountInfo accountInfo = this.getAccountByEmail(emailLoginRequest.getEmail());

        String rawPassword = emailLoginRequest.getPassword();
        String deviceId = emailLoginRequest.getDeviceId();

        this.validateAccount(accountInfo, rawPassword, deviceId);

        return getLoginResponse(accountInfo,deviceId);
    }

    public LoginResponse renewToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        String deviceId = refreshRequest.getDeviceId();

        RefreshToken accountRefreshToken = getRefreshToken(refreshToken,deviceId);

        AccountInfo accountInfo = accountRefreshToken.getAccountInfo();

        this.validateRefreshToken(accountInfo, accountRefreshToken);

        LocalDateTime now = LocalDateTime.now();

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(accountInfo.getId())
                .deviceId(deviceId)
                .jti(accountRefreshToken.getId())
                .permissions(List.of())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusMinutes(30).toEpochSecond(ZoneOffset.UTC))
                .roles(accountInfo.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();

        String token = jwtService.generateToken(jwtTokenClaims);

        return new LoginResponse(token, refreshToken);
    }

    private void validateAccount(AccountInfo accountInfo, String rawPassword, String deviceId) {
        if(accountInfo == null || !passwordEncoder.matches(rawPassword, accountInfo.getPassword())) {
            this.blockLoginAttempt(deviceId);
            throw new UnauthorizeException("Username or password is invalid");
        }

        if(accountInfo.getStatus() == AccountStatus.BANNED) {
            this.blockLoginAttempt(deviceId);
            throw new ForbiddenException("Your account is banned");
        }
    }

    private void checkLoginAttemps(String deviceId) {
        String key = "login:attemp:" + deviceId;
        Long attemps = redisService.get(key, Long.class);
        if(attemps != null && attemps > 3) {
            throw new TooManyRequestException("Too many login attempts");
        }
    }

    private void blockLoginAttempt(String deviceId) {
        String key = "login:attemp:" + deviceId;
        long attemps = redisService.increment(key, 1);
        redisService.expire(key, attemps > 3 ? 5 : 2, TimeUnit.MINUTES);
    }

    private void validateRefreshToken(AccountInfo accountInfo, RefreshToken refreshToken) {
        if(accountInfo.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }

        if(refreshToken.getRevoked()) {
            throw new UnauthorizeException("Refresh token is revoked");
        }

        if(refreshToken.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new UnauthorizeException("Refresh token is expired");
        }

        if(refreshToken.getIssuesAt().before(Timestamp.valueOf(accountInfo.getUpdatedTime()))) {
            throw new UnauthorizeException("Refresh token cannot be used");
        }
    }

    public LoginResponse getLoginResponse(AccountInfo accountInfo, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        JwtTokenClaims jwtTokenClaims;
        RefreshToken refreshToken = this.createNewRefreshToken(accountInfo, deviceId);

        jwtTokenClaims = JwtTokenClaims.builder()
                .id(accountInfo.getId())
                .deviceId(deviceId)
                .jti(refreshToken.getId())
                .permissions(List.of())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusMinutes(30).toEpochSecond(ZoneOffset.UTC))
                .roles(accountInfo.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();

        String jwt = jwtService.generateToken(jwtTokenClaims);

        return LoginResponse.builder()
                .refreshToken(refreshToken.getToken())
                .accessToken(jwt)
                .build();
    }

    protected abstract AccountInfo getAccountByUsername(String username);

    protected abstract AccountInfo getAccountByEmail(String email);

    protected abstract RefreshToken createNewRefreshToken(AccountInfo accountInfo, String deviceId);

    protected abstract RefreshToken getRefreshToken(String token, String deviceId);

}

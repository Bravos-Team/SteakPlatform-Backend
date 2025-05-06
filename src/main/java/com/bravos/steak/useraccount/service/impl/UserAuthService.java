package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserRefreshToken;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserRefreshTokenRepository;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service("userAuthService")
public class UserAuthService extends AuthService {

    private final UserAccountRepository userAccountRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public UserAuthService(RedisService redisService, PasswordEncoder passwordEncoder, JwtService jwtService,
                           HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,
                           UserAccountRepository userAccountRepository, SnowflakeGenerator snowflakeGenerator,
                           UserRefreshTokenRepository userRefreshTokenRepository) {
        super(redisService, passwordEncoder, jwtService, httpServletResponse, httpServletRequest);
        this.userAccountRepository = userAccountRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }


    @Override
    protected Account getAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    protected RefreshToken createRefreshToken(Account accountInfo, String deviceId, String deviceInfo) {
        LocalDateTime now = LocalDateTime.now();
        UserRefreshToken userRefreshToken = UserRefreshToken.builder()
                .id(snowflakeGenerator.generateId())
                .deviceId(deviceId)
                .deviceInfo(deviceInfo)
                .userAccount((UserAccount) accountInfo)
                .token(UUID.randomUUID().toString())
                .revoked(false)
                .issuesAt(Timestamp.valueOf(now))
                .expiresAt(Timestamp.valueOf(now.plus(refreshTokenDuration())))
                .build();
        return userRefreshTokenRepository.save(userRefreshToken);
    }

    @Override
    protected RefreshToken getRefreshToken(String token, String deviceId) {
        return userRefreshTokenRepository.findByTokenAndDeviceId(token, deviceId);
    }

    @Override
    protected Duration jwtDuration() {
        return Duration.parse(System.getProperty("USER_TOKEN_EXP"));
    }

    @Override
    protected Duration refreshTokenDuration() {
        return Duration.parse(System.getProperty("USER_REFRESH_TOKEN_EXP"));
    }

    @Override
    protected String getRole() {
        return "user";
    }

}

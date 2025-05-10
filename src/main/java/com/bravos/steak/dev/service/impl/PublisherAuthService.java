package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.entity.PublisherRefreshToken;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.repo.PublisherRefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component("publisherAuthService")
public class PublisherAuthService extends AuthService {

    private final PublisherAccountRepository publisherAccountRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final PublisherRefreshTokenRepository publisherRefreshTokenRepository;

    @Autowired
    public PublisherAuthService(RedisService redisService, PasswordEncoder passwordEncoder, JwtService jwtService,
                                HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,
                                PublisherAccountRepository publisherAccountRepository, SnowflakeGenerator snowflakeGenerator,
                                PublisherRefreshTokenRepository publisherRefreshTokenRepository) {
        super(redisService, passwordEncoder, jwtService, httpServletResponse, httpServletRequest);
        this.publisherAccountRepository = publisherAccountRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.publisherRefreshTokenRepository = publisherRefreshTokenRepository;
    }

    @Override
    protected Set<String> getCookiePaths() {
        return Set.of("/api/v1/store", "/api/v1/user", "/api/v1/support/user", "/api/v1/hub/user");
    }

    @Override
    protected Account getAccountByUsername(String username) {
        return publisherAccountRepository.findByUsername(username);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return publisherAccountRepository.findByEmail(email);
    }

    @Override
    protected RefreshToken createRefreshToken(Account account, String deviceId, String deviceInfo) {
        PublisherRefreshToken refreshToken = PublisherRefreshToken.builder()
                .id(snowflakeGenerator.generateId())
                .account((PublisherAccount) account)
                .token(UUID.randomUUID().toString())
                .deviceId(deviceId)
                .deviceInfo(deviceInfo)
                .expiresAt(LocalDateTime.now().plusSeconds(Long.parseLong(System.getProperty("USER_REFRESH_TOKEN_EXP"))))
                .revoked(false)
                .build();

        try {
            publisherRefreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            log.error("Error when creating refresh token: ", e);
            throw new RuntimeException("Error when creating token");
        }

        return refreshToken;
    }

    @Override
    protected RefreshToken getRefreshToken(String token, String deviceId) {
        try {
            return publisherRefreshTokenRepository.findByTokenAndDeviceId(token,deviceId);
        } catch (Exception e) {
            log.error("Error when get publisher refresh token: ",e);
            return null;
        }
    }

}

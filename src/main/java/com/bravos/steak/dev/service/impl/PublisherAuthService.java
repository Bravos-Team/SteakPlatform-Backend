package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.entity.PublisherRefreshToken;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.repo.PublisherRefreshTokenRepository;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.useraccount.model.request.OauthLoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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
                                HttpServletResponse httpServletResponse, PublisherAccountRepository publisherAccountRepository,
                                SnowflakeGenerator snowflakeGenerator, PublisherRefreshTokenRepository publisherRefreshTokenRepository,
                                SessionService sessionService) {
        super(redisService, passwordEncoder, jwtService, httpServletResponse, sessionService);
        this.publisherAccountRepository = publisherAccountRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.publisherRefreshTokenRepository = publisherRefreshTokenRepository;
    }

    @Override
    protected Set<String> getCookiePaths() {
        return Set.of("/api/v1/dev", "/api/v1/hub/publisher");
    }

    @Override
    protected Set<String> refreshPath() {
        return Set.of(
                "/api/v1/dev/auth/refresh",
                "/api/v1/dev/auth/logout"
        );
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
                .expiresAt(DateTimeHelper.from(DateTimeHelper.now().plusDays(30)))
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

    @Override
    protected Map<String, Object> otherClaims(Account account) {
        Long publisherId = ((PublisherAccount) account).getPublisher().getId();
        return Map.of("publisherId",publisherId);
    }

    @Override
    public void logout() {
        super.logout();
        String refreshToken = this.getRefreshToken();
        if (refreshToken != null) {
            PublisherRefreshToken publisherRefreshToken = publisherRefreshTokenRepository.findByToken(refreshToken);
            if (publisherRefreshToken != null) {
                publisherRefreshToken.setRevoked(true);
                publisherRefreshTokenRepository.save(publisherRefreshToken);
            }
        }
    }

    @Override
    public Account oauthLogin(OauthLoginRequest oauthLoginRequest) {
        throw new BadRequestException("OAuth login is not supported for publishers.");
    }

}

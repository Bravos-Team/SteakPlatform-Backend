package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.UserAccount;
import com.bravos.steak.account.entity.UserRefreshToken;
import com.bravos.steak.account.repo.UserAccountRepository;
import com.bravos.steak.account.repo.UserRefreshTokenRepository;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.model.AccountInfo;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserAuthService extends AuthService {

    private final SnowflakeGenerator generalIdGenerator;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserAccountRepository userAccountRepository;

    public UserAuthService(RedisService redisService, PasswordEncoder passwordEncoder, JwtService jwtService,
                              SnowflakeGenerator generalIdGenerator, UserRefreshTokenRepository userRefreshTokenRepository,
                           UserAccountRepository userAccountRepository) {
        super(redisService, passwordEncoder, jwtService);
        this.generalIdGenerator = generalIdGenerator;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    protected AccountInfo getAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    protected AccountInfo getAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    protected RefreshToken createNewRefreshToken(AccountInfo accountInfo, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        UserRefreshToken userRefreshToken = UserRefreshToken.builder()
                .id(generalIdGenerator.generateId())
                .deviceId(deviceId)
                .userAccount((UserAccount) accountInfo)
                .token(UUID.randomUUID().toString())
                .revoked(false)
                .issuesAt(Timestamp.valueOf(now))
                .expiresAt(Timestamp.valueOf(now.plusDays(30)))
                .build();
        return userRefreshTokenRepository.save(userRefreshToken);
    }

    @Override
    protected RefreshToken getRefreshToken(String token, String deviceId) {
        return userRefreshTokenRepository.findByTokenAndDeviceId(token,deviceId);
    }

}

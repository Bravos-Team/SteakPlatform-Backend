package com.bravos.steak.administration.service;

import com.bravos.steak.administration.entity.AdminAccount;
import com.bravos.steak.administration.entity.AdminRefreshToken;
import com.bravos.steak.administration.repo.AdminAccountRepository;
import com.bravos.steak.administration.repo.AdminRefreshTokenRepository;
import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AdminAuthService extends AuthService {

    private final AdminAccountRepository adminAccountRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final AdminRefreshTokenRepository adminRefreshTokenRepository;
    private final SessionService sessionService;

    @Autowired
    public AdminAuthService(RedisService redisService, PasswordEncoder passwordEncoder, JwtService jwtService,
                            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,
                            AdminAccountRepository adminAccountRepository, SnowflakeGenerator snowflakeGenerator,
                            AdminRefreshTokenRepository adminRefreshTokenRepository, SessionService sessionService) {
        super(redisService, passwordEncoder, jwtService, httpServletResponse, httpServletRequest);
        this.adminAccountRepository = adminAccountRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.adminRefreshTokenRepository = adminRefreshTokenRepository;
        this.sessionService = sessionService;
    }

    @Override
    protected Set<String> getCookiePaths() {
        return Set.of("/api/v1/admin");
    }

    @Override
    protected String refreshPath() {
        return "/api/v1/admin/auth/refresh";
    }

    @Override
    protected Account getAccountByUsername(String username) {
        return adminAccountRepository.findByUsername(username);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return adminAccountRepository.findByEmail(email);
    }

    @Override
    protected RefreshToken createRefreshToken(Account account, String deviceId, String deviceInfo) {
        AdminRefreshToken adminRefreshToken = AdminRefreshToken.builder()
                .id(snowflakeGenerator.generateId())
                .account((AdminAccount) account)
                .token(UUID.randomUUID().toString())
                .deviceId(deviceId)
                .deviceInfo(deviceInfo)
                .expiresAt(LocalDateTime.now().plusSeconds(Long.parseLong(System.getProperty("USER_REFRESH_TOKEN_EXP"))))
                .revoked(false)
                .build();

        try {
            adminRefreshToken = adminRefreshTokenRepository.save(adminRefreshToken);
        } catch (Exception e) {
            log.error("Error when creating refresh token: ", e);
            throw new RuntimeException("Error when creating token");
        }

        return adminRefreshToken;
    }

    @Override
    protected RefreshToken getRefreshToken(String token, String deviceId) {
        return adminRefreshTokenRepository.findByTokenAndDeviceId(token,deviceId);
    }

    @Override
    public void logout() {
        sessionService.logout("ADMIN");
    }

}

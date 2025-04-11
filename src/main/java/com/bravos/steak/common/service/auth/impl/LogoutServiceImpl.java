package com.bravos.steak.common.service.auth.impl;

import com.bravos.steak.account.repo.AccountRefreshTokenRepository;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.LogoutService;
import com.bravos.steak.common.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final RedisService redisService;
    private final AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Override
    public void killRefreshToken(long jti, String role) {
        try {

            if(role.equalsIgnoreCase("USER")) {
                accountRefreshTokenRepository.findById(jti).ifPresent(accountRefreshToken -> {
                    accountRefreshToken.setRevoked(true);
                    accountRefreshTokenRepository.save(accountRefreshToken);
                });
                return;
            }

            if (role.equalsIgnoreCase("ADMIN")) {
                // Implement kill refreshToken like revoke all refresh token of all user
                return;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void addBlacklistRefreshToken(long jti) {
        String key = "blacklist:jti:" + jti;
        redisService.save(key, jti, 30, TimeUnit.MINUTES);
    }

    @Override
    public boolean isRefreshTokenBlacklisted(long jti) {
        String key = "blacklist:jti:" + jti;
        return redisService.hasKey(key);
    }

    @Override
    public void logout() {

        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();

        try {
            JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();

            if (jwtTokenClaims != null) {
                Thread.startVirtualThread(() -> {
                    long jti = jwtTokenClaims.getJti();

                    this.killRefreshToken(jti, jwtTokenClaims.getRoles().getFirst());

                    this.addBlacklistRefreshToken(jti);
                }).start();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}

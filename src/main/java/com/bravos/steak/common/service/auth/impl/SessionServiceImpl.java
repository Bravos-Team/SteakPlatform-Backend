package com.bravos.steak.common.service.auth.impl;

import com.bravos.steak.useraccount.repo.UserRefreshTokenRepository;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final RedisService redisService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    @Override
    public void killRefreshToken(long jti, String role) {
        try {
            if(role.equalsIgnoreCase("USER")) {
                userRefreshTokenRepository.findById(jti).ifPresent(accountRefreshToken -> {
                    accountRefreshToken.setRevoked(true);
                    userRefreshTokenRepository.save(accountRefreshToken);
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
    public boolean isTokenBlacklisted(long jti) {
        String key = "blacklist:jti:" + jti;
        return redisService.hasKey(key);
    }

    @Override
    public void logout(String role) {
        JwtAuthentication authentication;
        try {
            authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            return;
        }

        if (authentication != null && authentication.getDetails() != null) {
            try {
                JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();

                Thread.startVirtualThread(() -> {
                    long jti = jwtTokenClaims.getJti();

                    this.killRefreshToken(jti, jwtTokenClaims.getRoles().stream().toList().getFirst());

                    this.addBlacklistRefreshToken(jti);
                });

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

}

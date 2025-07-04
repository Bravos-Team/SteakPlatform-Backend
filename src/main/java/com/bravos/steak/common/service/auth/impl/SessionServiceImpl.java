package com.bravos.steak.common.service.auth.impl;

import com.bravos.steak.administration.repo.AdminRefreshTokenRepository;
import com.bravos.steak.common.service.webhook.DiscordWebhookService;
import com.bravos.steak.dev.repo.PublisherRefreshTokenRepository;
import com.bravos.steak.useraccount.repo.UserRefreshTokenRepository;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.redis.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AdminRefreshTokenRepository adminRefreshTokenRepository;
    private final PublisherRefreshTokenRepository publisherRefreshTokenRepository;
    private final DiscordWebhookService discordWebhookService;
    private final HttpServletRequest httpServletRequest;

    @Override
    public JwtAuthentication getAuthentication() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void killRefreshToken(long jti, String role) {
        try {
            if(role.equalsIgnoreCase("USER")) {
                userRefreshTokenRepository.findById(jti).ifPresent(accountRefreshToken -> {
                    accountRefreshToken.setRevoked(true);
                    userRefreshTokenRepository.save(accountRefreshToken);
                });
            } else if (role.equalsIgnoreCase("ADMIN")) {
                adminRefreshTokenRepository.findById(jti).ifPresent(adminRefreshToken -> {
                    adminRefreshToken.setRevoked(true);
                    adminRefreshTokenRepository.save(adminRefreshToken);
                });
            } else if (role.equalsIgnoreCase("PUBLISHER")) {
                publisherRefreshTokenRepository.findById(jti).ifPresent(publisherRefreshToken -> {
                    publisherRefreshToken.setRevoked(true);
                    publisherRefreshTokenRepository.save(publisherRefreshToken);
                });
            } else {
                log.error("Unknown role: {}", role);
                throw new IllegalArgumentException("Unknown role: " + role);
            }
        } catch (Exception e) {
            log.error("Error when killing refresh token: {}", e.getMessage());
            discordWebhookService.sendError("Error when killing refresh token: " + e.getMessage(), e);
        }
    }

    @Override
    public void addBlacklistJti(long jti, long expireTime, TimeUnit timeUnit) {
        String key = "blacklist:jti:" + jti;
        redisService.save(key, jti, expireTime, TimeUnit.MINUTES);
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
            return;
        }

        if (authentication != null && authentication.getDetails() != null) {
            try {
                JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
                long jti = jwtTokenClaims.getJti();
                Thread.startVirtualThread(() -> {
                    this.killRefreshToken(jti, role);
                    this.addBlacklistJti(jti,30, TimeUnit.MINUTES);
                });
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public Cookie getCookie(String name) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

}

package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.security.JwtAuthentication;

import java.util.concurrent.TimeUnit;

public interface SessionService {

    JwtAuthentication getAuthentication();

    void killRefreshToken(long jti, String role);

    void addBlacklistJti(long jti, long expireTime, TimeUnit timeUnit);

    boolean isTokenBlacklisted(long jti);

    void logout(String role);

}

package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.security.JwtAuthentication;

public interface SessionService {

    JwtAuthentication getAuthentication();

    void killRefreshToken(long jti, String role);

    void addBlacklistRefreshToken(long jti);

    boolean isTokenBlacklisted(long jti);

    void logout(String role);

}

package com.bravos.steak.common.service.auth;

public interface SessionService {

    void killRefreshToken(long jti, String role);

    void addBlacklistRefreshToken(long jti);

    boolean isTokenBlacklisted(long jti);

    void logout();

}

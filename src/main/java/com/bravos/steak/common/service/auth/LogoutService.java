package com.bravos.steak.common.service.auth;

public interface LogoutService {

    void killRefreshToken(long jti, String role);

    void addBlacklistRefreshToken(long jti);

    boolean isRefreshTokenBlacklisted(long jti);

    void logout();

}

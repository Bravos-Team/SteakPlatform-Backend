package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.security.JwtAuthentication;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.util.concurrent.TimeUnit;

public interface SessionService {

    JwtAuthentication getAuthentication();

    Long getCurrentUserId();

    String getUserIpAddress();

    void addBlacklistJti(long jti, long expireTime, TimeUnit timeUnit);

    boolean isTokenBlacklisted(long jti);

    void logout();

    Cookie getCookie(String name);

    void addCookie(ResponseCookie cookie);

    void invalidateUserToken(Long id);

    boolean isInvalidToken(Long id, Long iat);

}

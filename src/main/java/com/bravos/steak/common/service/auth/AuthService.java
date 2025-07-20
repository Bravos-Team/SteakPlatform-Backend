package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.TooManyRequestException;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public abstract class AuthService {

    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;

    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";
    private final SessionService sessionService;

    public Account login(UsernameLoginRequest usernameLoginRequest) {

        this.checkLoginAttemptsLimit(usernameLoginRequest.getDeviceId());

        Account account = this.getAccountByUsername(usernameLoginRequest.getUsername());

        String rawPassword = usernameLoginRequest.getPassword();
        String deviceId = usernameLoginRequest.getDeviceId();
        String deviceInfo = usernameLoginRequest.getDeviceInfo();

        this.validateAccount(account, rawPassword, deviceId);

        this.generateAndAttachCredentials(account, deviceId, deviceInfo);

        return account;

    }

    public Account login(EmailLoginRequest emailLoginRequest) {
        this.checkLoginAttemptsLimit(emailLoginRequest.getDeviceId());

        Account account = this.getAccountByEmail(emailLoginRequest.getEmail());

        String rawPassword = emailLoginRequest.getPassword();
        String deviceId = emailLoginRequest.getDeviceId();
        String deviceInfo = emailLoginRequest.getDeviceInfo();

        this.validateAccount(account, rawPassword, deviceId);

        this.generateAndAttachCredentials(account, deviceId, deviceInfo);

        return account;
    }

    private void generateAndAttachCredentials(Account accountInfo, String deviceId, String deviceInfo) {
        RefreshToken refreshToken = createRefreshToken(accountInfo,deviceId, deviceInfo);
        String jwt = generateJwtToken(accountInfo,refreshToken.getId());

        Set<String> paths = getCookiePaths();
        for (String path : paths) {
            this.addAccessTokenCookie(jwt, path);
        }
        this.addRefreshTokenCookie(refreshToken);
    }

    private void validateAccount(Account accountInfo, String rawPassword, String deviceId) {
        if(accountInfo == null || accountInfo.getStatus() == AccountStatus.DELETED) {
            this.recordFailedLoginAttempt(deviceId);
            throw new UnauthorizeException("Username or password is invalid");
        }

        if(!passwordEncoder.matches(rawPassword, accountInfo.getPassword())) {
            this.recordFailedLoginAttempt(deviceId);
            throw new UnauthorizeException("Username or password is invalid");
        }

        if(accountInfo.getStatus() == AccountStatus.BANNED) {
            this.recordFailedLoginAttempt(deviceId);
            throw new ForbiddenException("Your account is banned");
        }
    }

    private void checkLoginAttemptsLimit(String deviceId) {
        String key = "login:attempt:" + deviceId;
        Long attempts;
        try {
            attempts = redisService.get(key, Long.class);
        } catch (Exception e) {
            log.error("Error when checking login attemps: {}", e.getMessage(), e);
            throw new RuntimeException("Error when checking login attemp");
        }
        if(attempts != null && attempts > 100) {
            throw new TooManyRequestException("Too many login attempts");
        }
    }

    private void recordFailedLoginAttempt(String deviceId) {
        String key = "login:attempt:" + deviceId;
        long attempts = redisService.increment(key, 1);
        redisService.expire(key, attempts > 3 ? 5 : 2, TimeUnit.MINUTES);
    }

    public Account renewToken(RefreshRequest refreshRequest) {

        this.checkLoginAttemptsLimit(refreshRequest.getDeviceId());

        String refreshToken = getRefreshToken();

        if(refreshToken == null || refreshToken.isBlank()) {
            this.recordFailedLoginAttempt(refreshRequest.getDeviceId());
            throw new UnauthorizeException("Refresh token is invalid");
        }

        String deviceId = refreshRequest.getDeviceId();

        RefreshToken accountRefreshToken = getRefreshToken(refreshToken,deviceId);

        if(accountRefreshToken == null) {
            this.recordFailedLoginAttempt(deviceId);
            throw new UnauthorizeException("Refresh token is invalid");
        }

        if(!accountRefreshToken.getDeviceId().equals(deviceId)) {
            throw new UnauthorizeException("Refresh token is invalid for this device");
        }

        Account account = accountRefreshToken.getAccount();

        this.validateRefreshToken(account, accountRefreshToken);

        String token = generateJwtToken(account,accountRefreshToken.getId());

        Set<String> paths = getCookiePaths();
        for (String path : paths) {
            this.addAccessTokenCookie(token, path);
            this.addRefreshTokenCookie(accountRefreshToken);
        }

        return account;
    }

    protected abstract Set<String> getCookiePaths();

    private String getRefreshToken() {
        Cookie cookie = sessionService.getCookie(REFRESH_TOKEN_NAME);
        return cookie != null ? cookie.getValue() : null;
    }

    private void validateRefreshToken(Account account, RefreshToken refreshToken) {
        if(account.getStatus() == AccountStatus.BANNED) {
            this.recordFailedLoginAttempt(refreshToken.getDeviceId());
            throw new ForbiddenException("Your account is banned");
        }

        if(refreshToken.getRevoked()) {
            this.recordFailedLoginAttempt(refreshToken.getDeviceId());
            throw new UnauthorizeException("Refresh token is revoked");
        }

        long currentTimeMillis = DateTimeHelper.currentTimeMillis();

        if(refreshToken.getExpiresAt() < currentTimeMillis) {
            this.recordFailedLoginAttempt(refreshToken.getDeviceId());
            throw new UnauthorizeException("Refresh token is expired");
        }

        if(refreshToken.getIssuesAt() > currentTimeMillis) {
            this.recordFailedLoginAttempt(refreshToken.getDeviceId());
            throw new UnauthorizeException("Refresh token cannot be used");
        }
    }

    private String generateJwtToken(Account account, long jti) {
        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .jti(jti)
                .authorities(account.getPermissions())
                .iat(DateTimeHelper.currentTimeMillis())
                .exp(DateTimeHelper.from(DateTimeHelper.now().plusMinutes(30)))
                .role(account.getRole().getAuthority())
                .otherClaims(this.otherClaims(account))
                .build();
        return jwtService.generateToken(jwtTokenClaims);
    }

    private void addAccessTokenCookie(String jwt, String path) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_NAME, jwt)
                .httpOnly(true)
                .secure(true)
                .path(path)
//                .domain(System.getProperty("COOKIE_DOMAIN"))
                .sameSite("None")
                .maxAge(Duration.ofMinutes(30))
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void addRefreshTokenCookie(RefreshToken refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path(refreshPath())
//                .domain(System.getProperty("COOKIE_DOMAIN"))
                .sameSite("None")
                .maxAge(Duration.ofDays(30))
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    protected abstract String refreshPath();

    protected abstract Account getAccountByUsername(String username);

    protected abstract Account getAccountByEmail(String email);

    protected abstract RefreshToken createRefreshToken(Account account, String deviceId, String deviceInfo);

    protected abstract RefreshToken getRefreshToken(String token, String deviceId);

    protected Map<String,Object> otherClaims(Account account) {
        return Map.of();
    }

    public abstract void logout();

}

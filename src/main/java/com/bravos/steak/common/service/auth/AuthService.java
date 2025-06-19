package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.TooManyRequestException;
import com.bravos.steak.exceptions.UnauthorizeException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
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

    private final HttpServletRequest httpServletRequest;

    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

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
            this.addRefreshTokenCookie(refreshToken, path);
        }
    }

    private void validateAccount(Account accountInfo, String rawPassword, String deviceId) {
        if(accountInfo == null || !passwordEncoder.matches(rawPassword, accountInfo.getPassword())) {
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
        if(attempts != null && attempts > 3) {
            throw new TooManyRequestException("Too many login attempts");
        }
    }

    private void recordFailedLoginAttempt(String deviceId) {
        String key = "login:attempt:" + deviceId;
        long attempts = redisService.increment(key, 1);
        redisService.expire(key, attempts > 3 ? 5 : 2, TimeUnit.MINUTES);
    }

    public Long renewToken(RefreshRequest refreshRequest) {
        String refreshToken = getRefreshToken();

        if(refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizeException("Refresh token is invalid");
        }

        String deviceId = refreshRequest.getDeviceId();

        RefreshToken accountRefreshToken = getRefreshToken(refreshToken,deviceId);

        if(accountRefreshToken == null) {
            throw new UnauthorizeException("Refresh token is invalid");
        }

        Account account = accountRefreshToken.getAccount();

        this.validateRefreshToken(account, accountRefreshToken);

        String token = generateJwtToken(account,accountRefreshToken.getId());

        Set<String> paths = getCookiePaths();
        for (String path : paths) {
            this.addAccessTokenCookie(token, path);
            this.addRefreshTokenCookie(accountRefreshToken, path);
        }

        return account.getId();
    }

    protected abstract Set<String> getCookiePaths();

    private String getRefreshToken() {
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null) {
            Cookie refreshCookie = Arrays.stream(cookies).filter(cookie ->
                    cookie.getName().equals(REFRESH_TOKEN_NAME)).findFirst().orElse(null);
            if(refreshCookie != null) {
                return refreshCookie.getValue();
            }
            return null;
        }
        return null;
    }

    private void validateRefreshToken(Account account, RefreshToken refreshToken) {
        if(account.getStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Your account is banned");
        }

        if(refreshToken.getRevoked()) {
            throw new UnauthorizeException("Refresh token is revoked");
        }

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizeException("Refresh token is expired");
        }

        if(refreshToken.getIssuesAt().isBefore(account.getUpdatedAt())) {
            throw new UnauthorizeException("Refresh token cannot be used");
        }
    }

    private String generateJwtToken(Account account, long jti) {
        LocalDateTime now = LocalDateTime.now();
        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .jti(jti)
                .authorities(account.getPermissions())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusSeconds(Long.parseLong(System.getProperty("USER_TOKEN_EXP"))).toEpochSecond(ZoneOffset.UTC))
                .role(account.getRole().getAuthority())
                .otherClaims(this.otherClaims(account))
                .build();
        return jwtService.generateToken(jwtTokenClaims);
    }

    private void addAccessTokenCookie(String jwt, String path) {
        LocalDateTime now = LocalDateTime.now();
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_NAME, jwt)
                .httpOnly(true)
                .secure(false)
                .path(path)
                .domain(System.getProperty("COOKIE_DOMAIN"))
                .sameSite("Lax")
                .maxAge(now.plusSeconds(Long.parseLong(System.getProperty("USER_TOKEN_EXP"))).toEpochSecond(ZoneOffset.UTC))
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void addRefreshTokenCookie(RefreshToken refreshToken, String path) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path(path)
                .domain(System.getProperty("COOKIE_DOMAIN"))
                .sameSite("Lax")
                .maxAge(refreshToken.getExpiresAt().toEpochSecond(ZoneOffset.UTC))
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    protected abstract Account getAccountByUsername(String username);

    protected abstract Account getAccountByEmail(String email);

    protected abstract RefreshToken createRefreshToken(Account account, String deviceId, String deviceInfo);

    protected abstract RefreshToken getRefreshToken(String token, String deviceId);

    protected Map<String,Object> otherClaims(Account account) {
        return Map.of();
    }

}

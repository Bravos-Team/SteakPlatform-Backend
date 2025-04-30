package com.bravos.steak.common.service.auth;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.model.request.EmailLoginRequest;
import com.bravos.steak.useraccount.model.request.RefreshRequest;
import com.bravos.steak.useraccount.model.request.UsernameLoginRequest;
import com.bravos.steak.useraccount.model.response.LoginResponse;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.TooManyRequestException;
import com.bravos.steak.exceptions.UnauthorizeException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public abstract class AuthService {

    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;
    private final UserProfileRepository userProfileRepository;

    private final String jwtCookieName = "jwt";
    private final String refreshTokenCookieName = "refresh_token";
    private final HttpServletRequest httpServletRequest;

    public LoginResponse login(UsernameLoginRequest usernameLoginRequest) {

        this.checkLoginAttemptsLimit(usernameLoginRequest.getDeviceId());

        Account account = this.getAccountByUsername(usernameLoginRequest.getUsername());
        String rawPassword = usernameLoginRequest.getPassword();
        String deviceId = usernameLoginRequest.getDeviceId();
        String deviceInfo = usernameLoginRequest.getDeviceInfo();

        this.validateAccount(account, rawPassword, deviceId);

        this.generateAndAttachCredentials(account, deviceId, deviceInfo);

        return buildLoginResponse(account.getId());

    }

    public LoginResponse login(EmailLoginRequest emailLoginRequest) {
        this.checkLoginAttemptsLimit(emailLoginRequest.getDeviceId());

        Account account = this.getAccountByEmail(emailLoginRequest.getEmail());

        String rawPassword = emailLoginRequest.getPassword();
        String deviceId = emailLoginRequest.getDeviceId();
        String deviceInfo = emailLoginRequest.getDeviceInfo();

        this.validateAccount(account, rawPassword, deviceId);

        this.generateAndAttachCredentials(account, deviceId, deviceInfo);

        return buildLoginResponse(account.getId());
    }

    private void generateAndAttachCredentials(Account accountInfo, String deviceId, String deviceInfo) {
        RefreshToken refreshToken = createRefreshToken(accountInfo,deviceId, deviceInfo);
        String jwt = generateJwtToken(accountInfo,refreshToken.getDeviceId(),refreshToken.getId());
        this.addCredentialsToCookie(jwt,refreshToken.getToken());
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
        Long attempts = redisService.get(key, Long.class);
        if(attempts != null && attempts > 3) {
            throw new TooManyRequestException("Too many login attempts");
        }
    }

    private void recordFailedLoginAttempt(String deviceId) {
        String key = "login:attempt:" + deviceId;
        long attempts = redisService.increment(key, 1);
        redisService.expire(key, attempts > 3 ? 5 : 2, TimeUnit.MINUTES);
    }

    public LoginResponse renewToken(RefreshRequest refreshRequest) {
        String refreshToken = getRefreshToken();

        if(refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizeException("Refresh token is invalid");
        }

        String deviceId = refreshRequest.getDeviceId();

        RefreshToken accountRefreshToken = getRefreshToken(refreshToken,deviceId);

        Account account = accountRefreshToken.getAccount();

        this.validateRefreshToken(account, accountRefreshToken);

        LocalDateTime now = LocalDateTime.now();

        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .deviceId(deviceId)
                .jti(accountRefreshToken.getId())
                .permissions(List.of())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plusMinutes(30).toEpochSecond(ZoneOffset.UTC))
                .roles(account.getRoles().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();

        String token = jwtService.generateToken(jwtTokenClaims);

        return new LoginResponse(token, refreshToken);
    }

    private String getRefreshToken() {
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null) {
            Cookie refreshCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(jwtCookieName)).findFirst().orElse(null);
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

        if(refreshToken.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new UnauthorizeException("Refresh token is expired");
        }

        if(refreshToken.getIssuesAt().before(Timestamp.valueOf(account.getUpdatedAt()))) {
            throw new UnauthorizeException("Refresh token cannot be used");
        }
    }

    private String generateJwtToken(Account account, String deviceId, long jti) {
        LocalDateTime now = LocalDateTime.now();
        JwtTokenClaims jwtTokenClaims = JwtTokenClaims.builder()
                .id(account.getId())
                .deviceId(deviceId)
                .jti(jti)
                .permissions(account.getPermissions())
                .iat(now.toEpochSecond(ZoneOffset.UTC))
                .exp(now.plus(Duration.parse(System.getProperty("USER_TOKEN_EXP"))).toEpochSecond(ZoneOffset.UTC))
                .roles(account.getRoles().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                ).build();
        return jwtService.generateToken(jwtTokenClaims);
    }

    private void addCredentialsToCookie(String jwt, String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(refreshTokenDuration())
                .build();
        ResponseCookie accessCookie = ResponseCookie.from(jwtCookieName, jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtDuration())
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE,accessCookie.toString());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE,refreshCookie.toString());
    }

    public LoginResponse buildLoginResponse(Long accountId) {
        UserProfile userProfile = userProfileRepository.findById(accountId).orElse(new UserProfile());
        return new LoginResponse(userProfile.getDisplayName(),userProfile.getAvatarUrl());
    }

    protected abstract Account getAccountByUsername(String username);

    protected abstract Account getAccountByEmail(String email);

    protected abstract RefreshToken createRefreshToken(Account account, String deviceId, String deviceInfo);

    protected abstract RefreshToken getRefreshToken(String token, String deviceId);

    protected abstract Duration jwtDuration();

    protected abstract Duration refreshTokenDuration();

}

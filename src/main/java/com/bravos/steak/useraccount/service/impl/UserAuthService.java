package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.service.auth.AuthService;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserOauth2Account;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.entity.UserRefreshToken;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.model.enums.Oauth2Status;
import com.bravos.steak.useraccount.model.request.OauthLoginRequest;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserOauth2AccountRepository;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.repo.UserRefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service("userAuthService")
public class UserAuthService extends AuthService {
    private final UserProfileRepository userProfileRepository;

    private final UserAccountRepository userAccountRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final SessionService sessionService;
    private final OAuth20Service googleOauthService;
    private final ObjectMapper objectMapper;
    private final UserOauth2AccountRepository userOauth2AccountRepository;


    private static final String GOOGLE_OAUTH_LOGIN_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GITHUB_OAUTH_LOGIN_URL = "https://api.github.com/user";
    private final OAuth20Service githubOauthService;

    @Autowired
    public UserAuthService(RedisService redisService, PasswordEncoder passwordEncoder, JwtService jwtService,
                           HttpServletResponse httpServletResponse,
                           UserAccountRepository userAccountRepository, SnowflakeGenerator snowflakeGenerator,
                           UserRefreshTokenRepository userRefreshTokenRepository, SessionService sessionService,
                           OAuth20Service googleOauthService, ObjectMapper objectMapper, UserOauth2AccountRepository userOauth2AccountRepository,
                           UserProfileRepository userProfileRepository, OAuth20Service githubOauthService) {
        super(redisService, passwordEncoder, jwtService, httpServletResponse, sessionService);
        this.userAccountRepository = userAccountRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.sessionService = sessionService;
        this.googleOauthService = googleOauthService;
        this.objectMapper = objectMapper;
        this.userOauth2AccountRepository = userOauth2AccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.githubOauthService = githubOauthService;
    }

    @Override
    protected Set<String> getCookiePaths() {
        return Set.of("/api/v1/store", "/api/v1/user", "/api/v1/support/user", "/api/v1/hub/user");
    }

    @Override
    protected String refreshPath() {
        return "/api/v1/user/auth/refresh";
    }

    @Override
    protected Account getAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    protected Account getAccountByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    @Override
    protected RefreshToken createRefreshToken(Account accountInfo, String deviceId, String deviceInfo) {
        UserRefreshToken userRefreshToken = UserRefreshToken.builder()
                .id(snowflakeGenerator.generateId())
                .deviceId(deviceId)
                .deviceInfo(deviceInfo)
                .userAccount((UserAccount) accountInfo)
                .token(UUID.randomUUID().toString())
                .revoked(false)
                .expiresAt(DateTimeHelper.from(DateTimeHelper.now().plusDays(30)))
                .build();
        try {
            return userRefreshTokenRepository.save(userRefreshToken);
        } catch (Exception e) {
            log.error("Error when creating refresh token: ", e);
            throw new RuntimeException("Error when creating token");
        }
    }

    @Override
    protected RefreshToken getRefreshToken(String token, String deviceId) {
        return userRefreshTokenRepository.findByTokenAndDeviceId(token, deviceId);
    }

    @Override
    public void logout() {
        sessionService.logout("USER");
    }

    @Override
    @Transactional
    public Account oauthLogin(OauthLoginRequest oauthLoginRequest) {
        if(!oauthLoginRequest.getDeviceId().equals(getDeviceIdFromOAuth2State(oauthLoginRequest.getState()))) {
            throw new BadRequestException("Invalid device ID in OAuth login request");
        }
        switch (oauthLoginRequest.getProvider()) {
            case "google" -> {
                return googleOauthLogin(oauthLoginRequest);
            }
            case "github" -> {
                return githubOauthLogin(oauthLoginRequest);
            }
            case null, default -> throw new BadRequestException("Invalid OAuth provider: " + oauthLoginRequest.getProvider());
        }
    }

    private Account googleOauthLogin(OauthLoginRequest oauthLoginRequest) {
        try {
            String code = oauthLoginRequest.getCode();
            log.info(googleOauthService.getAuthorizationUrl());
            log.info(googleOauthService.getApiKey());
            log.info(googleOauthService.getApiSecret());
            OAuth2AccessToken accessToken = googleOauthService.getAccessToken(code);
            log.info("Access token: {}",accessToken.getAccessToken());
            OAuthRequest request = new OAuthRequest(Verb.GET, GOOGLE_OAUTH_LOGIN_URL);
            googleOauthService.signRequest(accessToken, request);
            String response = googleOauthService.execute(request).getBody();
            GoogleOauthResponse googleOauthResponse = objectMapper.readValue(response, GoogleOauthResponse.class);
            if(googleOauthResponse.getEmail() == null || googleOauthResponse.getEmail().isBlank()) {
                throw new BadRequestException("Please provide a valid email address in your Google account.");
            }
            return handleOauth2Login(oauthLoginRequest, "google", googleOauthResponse.getSub(),
                    googleOauthResponse.getName(), googleOauthResponse.getEmail(), googleOauthResponse.getPicture());
        } catch (Exception e) {
            log.error("Error during Google OAuth login: ", e);
            throw new BadRequestException("Error during Google OAuth login: " + e.getMessage());
        }
    }

    private Account githubOauthLogin(OauthLoginRequest oauthLoginRequest) {
        try {
            OAuth2AccessToken accessToken = githubOauthService.getAccessToken(oauthLoginRequest.getCode());
            OAuthRequest request = new OAuthRequest(Verb.GET, GITHUB_OAUTH_LOGIN_URL);
            githubOauthService.signRequest(accessToken, request);
            String response = githubOauthService.execute(request).getBody();
            GithubOauthResponse githubOauthResponse = objectMapper.readValue(response, GithubOauthResponse.class);
            if(githubOauthResponse.getEmail() == null || githubOauthResponse.getEmail().isBlank()) {
                throw new BadRequestException("Please provide a valid email address and public they in your GitHub account.");
            }
            return handleOauth2Login(oauthLoginRequest, "github", String.valueOf(githubOauthResponse.getId()),
                    githubOauthResponse.getLogin(), githubOauthResponse.getEmail(), githubOauthResponse.getAvatar_url());
        } catch (Exception e) {
            log.error("Error during GitHub OAuth login: ", e);
            throw new BadRequestException("Error during GitHub OAuth login: " + e.getMessage());
        }
    }

    private Account handleOauth2Login(OauthLoginRequest oauthLoginRequest, String provider,
                                      String providerId, String name, String email, String picture) {
        Account account = getAccountByOauth(provider, providerId);
        if (account != null) {
            this.generateAndAttachCredentials(account, oauthLoginRequest.getDeviceId(), oauthLoginRequest.getDeviceInfo());
            return account;
        }

        account = userAccountRepository.findByEmail(email);
        if (account != null) {
            linkOauthAccountToUser(account, provider, providerId);
            this.generateAndAttachCredentials(account, oauthLoginRequest.getDeviceId(), oauthLoginRequest.getDeviceInfo());
            return account;
        }

        account = registerAccountWithOauth(provider, providerId, name, email, picture);
        linkOauthAccountToUser(account, provider, providerId);
        this.generateAndAttachCredentials(account, oauthLoginRequest.getDeviceId(), oauthLoginRequest.getDeviceInfo());

        return account;
    }

    private void linkOauthAccountToUser(Account account, String provider, String providerId) {
        UserOauth2Account userOauth2Account = UserOauth2Account.builder()
                .id(snowflakeGenerator.generateId())
                .oauth2Provider(provider)
                .oauth2Id(providerId)
                .status(Oauth2Status.ACTIVE)
                .userAccount((UserAccount) account)
                .build();
        try {
            userOauth2AccountRepository.save(userOauth2Account);
        } catch (Exception e) {
            throw new RuntimeException("Error when linking OAuth account to user: " + e.getMessage(), e);
        }
    }

    private Account registerAccountWithOauth(String provider, String providerId, String name, String email, String picture) {
        UserAccount userAccount = UserAccount.builder()
                .id(snowflakeGenerator.generateId())
                .username(provider + "-" + providerId)
                .email(email)
                .status(AccountStatus.ACTIVE)
                .password(this.getPasswordEncoder().encode(UUID.randomUUID().toString()))
                .build();

        try {
            userAccount = userAccountRepository.save(userAccount);
        } catch (Exception e) {
            throw new RuntimeException("Error when registering account with OAuth: " + e.getMessage(), e);
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setId(userAccount.getId());
        userProfile.setDisplayName(name);
        userProfile.setAvatarUrl(picture);

        try {
            userProfileRepository.save(userProfile);
        } catch (Exception e) {
            userAccountRepository.delete(userAccount);
            throw new RuntimeException("Error when saving user profile: " + e.getMessage(), e);
        }
        return userAccount;
    }

    private Account getAccountByOauth(String provider, String providerId) {
        UserOauth2Account userOauth2Account = userOauth2AccountRepository.findByOauth2ProviderAndOauth2Id(provider, providerId);
        if(userOauth2Account == null) {
            return null;
        }
        return userOauth2Account.getUserAccount();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GoogleOauthResponse {
        private String sub;
        private String name;
        private String given_name;
        private String family_name;
        private String picture;
        private String email;
        private boolean email_verified;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GithubOauthResponse {
        private String login;
        private long id;
        private String avatar_url;
        private String gravatar_id;
        private String email;
    }

}

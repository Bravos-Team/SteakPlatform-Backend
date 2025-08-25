package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final SessionService sessionService;
    private final UserProfileRepository userProfileRepository;
    private final UserAccountService userAccountService;
    private final RedisService redisService;

    public UserProfileServiceImpl(SessionService sessionService,
                                  UserProfileRepository userProfileRepository,
                                  UserAccountService userAccountService, RedisService redisService) {
        this.sessionService = sessionService;
        this.userProfileRepository = userProfileRepository;
        this.userAccountService = userAccountService;
        this.redisService = redisService;
    }

    @Override
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id).orElse(null);
    }

    @Override
    public UserProfile getUserProfile() {
        Long currentUserId = this.getCurrentUserId();
        String key = "user_profile_" + currentUserId;
        RedisCacheEntry<UserProfile> cacheEntry = RedisCacheEntry.<UserProfile>builder()
                .key(key)
                .fallBackFunction(() -> this.getUserProfile(currentUserId))
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(100)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        return redisService.getWithLock(cacheEntry, UserProfile.class);
    }

    public UserProfile getUserProfile(Long userId) {
        UserProfile profile = this.getUserProfileById(userId);
        if(profile == null) {
            UserAccount userAccount = userAccountService.getAccountById(userId);
            if (userAccount == null) {
                throw new ForbiddenException("User account not found.");
            }
            profile = new UserProfile();
            profile.setId(userId);
            profile.setDisplayName(userAccount.getUsername());
            userProfileRepository.save(profile);
        }
        return profile;
    }

    @Override
    public UserProfile updateUserProfile(UserProfile userProfile) {
        Long currentUserId = this.getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(userProfile.getId())) {
            try {
                userProfile = userProfileRepository.save(userProfile);
            } catch (Exception e) {
                log.error("Failed to update user profile", e);
                throw new RuntimeException("Failed to update user profile: " + e.getMessage());
            }
            String key = "user_profile_" + currentUserId;
            redisService.delete(key);
            return userProfile;
        }
        throw new ForbiddenException("You are not allowed to update this profile.");
    }

    private Long getCurrentUserId() {
        return sessionService.getCurrentUserId();
    }

}

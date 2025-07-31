package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final SessionService sessionService;
    private final UserProfileRepository userProfileRepository;
    private final UserAccountService userAccountService;

    public UserProfileServiceImpl(SessionService sessionService,
                                  UserProfileRepository userProfileRepository,
                                  UserAccountService userAccountService) {
        this.sessionService = sessionService;
        this.userProfileRepository = userProfileRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id).orElse(null);
    }

    @Override
    public UserProfile getUserProfile() {
        Long currentUserId = this.getCurrentUserId();
        UserProfile profile = this.getUserProfileById(currentUserId);
        if(profile == null) {
            UserAccount userAccount = userAccountService.getAccountById(currentUserId);
            if (userAccount == null) {
                throw new ForbiddenException("User account not found.");
            }
            profile = new UserProfile();
            profile.setId(currentUserId);
            profile.setDisplayName(userAccount.getUsername());
            userProfileRepository.save(profile);
        }
        return profile;
    }

    @Override
    public UserProfile updateUserProfile(UserProfile userProfile) {
        Long currentUserId = this.getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(userProfile.getId())) {
            userProfile = userProfileRepository.save(userProfile);
            return userProfile;
        }
        throw new ForbiddenException("You are not allowed to update this profile.");
    }

    private Long getCurrentUserId() {
        return sessionService.getCurrentUserId();
    }

}

package com.bravos.steak.useraccount.service;

import com.bravos.steak.useraccount.entity.UserProfile;

public interface UserProfileService {

    UserProfile getUserProfileById(Long id);

    UserProfile getUserProfile();

    UserProfile updateUserProfile(UserProfile userProfile);

}

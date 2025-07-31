package com.bravos.steak.useraccount.controller;

import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        return ResponseEntity.ok(userProfileService.getUserProfile());
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile updatedProfile = userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.ok(updatedProfile);
    }

}

package com.bravos.steak.useraccount.repo;

import com.bravos.steak.useraccount.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {


    UserRefreshToken findByTokenAndDeviceId(String token, String deviceId);
    
}
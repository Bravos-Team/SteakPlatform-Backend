package com.bravos.steak.account.repo;

import com.bravos.steak.account.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {


    UserRefreshToken findByTokenAndDeviceId(String token, String deviceId);
    
}
package com.bravos.steak.administration.repo;

import com.bravos.steak.administration.entity.AdminRefreshToken;
import com.bravos.steak.common.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshToken, Long> {
    RefreshToken findByTokenAndDeviceId(String token, String deviceId);

    AdminRefreshToken findByToken(String token);
}
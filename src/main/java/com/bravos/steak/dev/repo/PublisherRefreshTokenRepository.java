package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRefreshTokenRepository extends JpaRepository<PublisherRefreshToken, Long> {
  PublisherRefreshToken findByTokenAndDeviceId(String token, String deviceId);

    PublisherRefreshToken findByToken(String token);
}
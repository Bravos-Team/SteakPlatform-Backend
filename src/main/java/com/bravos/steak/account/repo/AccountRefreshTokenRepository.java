package com.bravos.steak.account.repo;

import com.bravos.steak.account.entity.AccountRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {

    Optional<AccountRefreshToken> findByTokenAndDeviceId(String token, String deviceId);

}
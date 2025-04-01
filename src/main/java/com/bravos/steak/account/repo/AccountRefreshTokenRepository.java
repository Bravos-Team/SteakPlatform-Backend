package com.bravos.steak.account.repo;

import com.bravos.steak.account.entity.AccountRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {
}
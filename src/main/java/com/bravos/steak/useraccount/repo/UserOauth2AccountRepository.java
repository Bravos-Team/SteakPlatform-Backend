package com.bravos.steak.useraccount.repo;

import com.bravos.steak.useraccount.entity.UserOauth2Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOauth2AccountRepository extends JpaRepository<UserOauth2Account, Long> {
    UserOauth2Account findByOauth2ProviderAndOauth2Id(String oauth2Provider, String oauth2Id);
}

package com.bravos.steak.administration.repo;

import com.bravos.steak.administration.entity.AdminAccount;
import com.bravos.steak.common.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAccountRepository extends JpaRepository<AdminAccount,Long> {
    Account findByUsername(String username);

    Account findByEmail(String email);
}

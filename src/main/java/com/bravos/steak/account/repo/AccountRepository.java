package com.bravos.steak.account.repo;

import com.bravos.steak.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>, JpaSpecificationExecutor<Account> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);

    Account findByUsername(String username);

    Account findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);
}

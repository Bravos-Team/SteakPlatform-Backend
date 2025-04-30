package com.bravos.steak.useraccount.repo;

import com.bravos.steak.useraccount.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Long>, JpaSpecificationExecutor<UserAccount> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);

    UserAccount findByUsername(String username);

    UserAccount findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);
}

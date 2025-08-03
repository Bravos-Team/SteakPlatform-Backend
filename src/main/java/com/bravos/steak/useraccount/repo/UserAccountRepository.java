package com.bravos.steak.useraccount.repo;

import com.bravos.steak.administration.model.response.UserListItem;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);

    UserAccount findByUsername(String username);

    UserAccount findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT new com.bravos.steak.administration.model.response.UserListItem(u.id, u.username, u.email, u.status) " +
           "FROM UserAccount u")
    Page<UserListItem> getAllUsers(Pageable pageable);

    @Query("SELECT new com.bravos.steak.administration.model.response.UserListItem(u.id, u.username, u.email, u.status) " +
           "FROM UserAccount u WHERE u.username LIKE %:query%")
    Page<UserListItem> getUsersByUsername(String username, Pageable pageable);

    @Query("SELECT new com.bravos.steak.administration.model.response.UserListItem(u.id, u.username, u.email, u.status) " +
           "FROM UserAccount u WHERE u.email LIKE %:email%")
    Page<UserListItem> getUsersByEmail(String email, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update UserAccount u set u.status = ?1 where u.id = ?2")
    int updateStatusById(AccountStatus status, Long id);

}

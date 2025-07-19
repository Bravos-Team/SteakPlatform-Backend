package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherAccountRepository extends JpaRepository<PublisherAccount,Long> {

    boolean existsByEmailOrUsername(String email, String username);

    PublisherAccount findByUsername(String username);

    PublisherAccount findByEmail(String email);

    @Query("SELECT new com.bravos.steak.dev.model.response.PublisherAccountListItem(" +
            "p.id, p.username, p.email) " +
            "FROM PublisherAccount p " +
            "WHERE p.status = :status")
    Page<PublisherAccountListItem> findAllByStatus(@Param("status") AccountStatus status, Pageable pageable);

    @Query("SELECT new com.bravos.steak.dev.model.response.PublisherAccountListItem(" +
            "p.id, p.username, p.email) " +
            "FROM PublisherAccount p")
    Page<PublisherAccountListItem> findAllz(Pageable pageable);

}

package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.repo.injection.PublisherAccountInfoListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherAccountRepository extends JpaRepository<PublisherAccount,Long> {

    boolean existsByEmailOrUsername(String email, String username);

    PublisherAccount findByUsername(String username);

    PublisherAccount findByEmail(String email);

    @Query("SELECT new com.bravos.steak.dev.repo.injection.PublisherAccountInfoListItem(pa.id,pa.username,pa.email,pa.roles) " +
            "FROM PublisherAccount pa " +
            "WHERE pa.status = :status")
    Page<PublisherAccountInfoListItem> getListItem(Pageable pageable, String status);

}

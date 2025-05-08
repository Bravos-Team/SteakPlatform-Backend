package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherAccountRepository extends JpaRepository<PublisherAccount,Long> {

    boolean existsByEmailOrUsername(String email, String username);

    PublisherAccount findByUsername(String username);

    PublisherAccount findByEmail(String email);

}

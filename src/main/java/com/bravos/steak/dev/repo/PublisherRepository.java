package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher,Long> {
    boolean existsByName(String name);

    boolean existsByNameOrEmail(String name, String email);
}

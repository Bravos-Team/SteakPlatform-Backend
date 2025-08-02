package com.bravos.steak.dev.repo;

import com.bravos.steak.administration.model.response.PublisherListItem;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.dev.model.enums.PublisherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher,Long> {

    boolean existsByName(String name);

    boolean existsByNameOrEmail(String name, String email);

    @Query("SELECT new com.bravos.steak.administration.model.response.PublisherListItem(p.id, p.name, p.email, p.status) " +
           "FROM Publisher p")
    Page<PublisherListItem> getAllPublishers(Pageable pageable);

    @Query("SELECT new com.bravos.steak.administration.model.response.PublisherListItem(p.id, p.name, p.email, p.status) " +
           "FROM Publisher p WHERE p.name LIKE %:name%")
    Page<PublisherListItem> getAllPublishersByName(String name, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Publisher p set p.status = ?1 where p.id = ?2")
    int updateStatusById(PublisherStatus status, Long id);
}

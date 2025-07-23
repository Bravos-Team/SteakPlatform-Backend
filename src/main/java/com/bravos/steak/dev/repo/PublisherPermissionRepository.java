package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PublisherPermissionRepository extends JpaRepository<PublisherPermission, Long> {

    Set<PublisherPermission> findAllByIdIn(Collection<Long> ids);

    Optional<PublisherPermission> findByName(String name);

}
package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherPermissionGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherPermissionGroupRepository extends JpaRepository<PublisherPermissionGroup, Long> {

    @Query("SELECT ppg FROM PublisherPermissionGroup ppg")
    @EntityGraph(attributePaths = {"publisherPermissionList"})
    List<PublisherPermissionGroup> findAllAndDetails();

}
package com.bravos.steak.dev.repo;

import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PublisherRoleRepository extends JpaRepository<PublisherRole, Long> {

    @Query("SELECT count (pr) FROM PublisherRole pr " +
           "WHERE (pr.publisher.id = :publisherId or pr.publisher.id IS NULL) AND pr.id IN :roleIds")
    Long countRolesAvailableByPublisherId(Long publisherId, Set<Long> roleIds);

    List<PublisherRole> findAllByPublisherId(Long publisherId);

    @Query("SELECT pr FROM PublisherRole pr " +
           "WHERE pr.id = :roleId AND (pr.publisher.id = :publisherId OR pr.publisher.id IS NULL)")
    PublisherRole findAvailableRoleByIdAndPublisherId(Long roleId, Long publisherId);

    @Query("SELECT new com.bravos.steak.dev.model.response.PublisherAccountListItem(" +
           "pa.id, pa.username, pa.email) " +
           "FROM PublisherAccount pa " +
           "JOIN pa.roles pr " +
           "WHERE pr.id = :roleId AND" +
            " (pr.publisher.id = :publisherId OR pr.publisher.id IS NULL) AND" +
            " pa.publisher.id = :publisherId")
    List<PublisherAccountListItem> findAssignedAccountsByRoleIdAndPublisherId(Long roleId, Long publisherId);

}
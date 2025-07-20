package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAccountId(Long userAccountId);

    void deleteByUserAccountId(Long userAccountId);

    @Transactional
    @Modifying
    @Query("update Cart c set c.updatedAt = ?1 where c.id = ?2")
    void updateUpdatedAtById(Long updatedAt, Long id);

}
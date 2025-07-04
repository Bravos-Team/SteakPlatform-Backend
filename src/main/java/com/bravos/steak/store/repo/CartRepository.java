package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserAccountId(Long userAccountId);

  void removeById(Long id);

  void removeByUserAccountId(Long userAccountId);
}
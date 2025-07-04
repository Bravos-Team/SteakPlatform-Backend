package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  void removeCartItemByGameIdAndCartId(Long gameId, Long cartId);
}
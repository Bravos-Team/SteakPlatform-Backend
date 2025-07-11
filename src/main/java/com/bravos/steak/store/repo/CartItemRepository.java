package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void removeCartItemByGameIdAndCartId(Long gameId, Long cartId);

    void removeCartItemByCartIdAndGameIdIn(Long cartId, List<Long> gameIds);

    List<CartItem> findAllByCartId(Long cartId);
}
package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.CartItem;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GamePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void removeCartItemByGameIdAndCartId(Long gameId, Long cartId);

    void removeCartItemByCartIdAndGameIdIn(Long cartId, List<Long> gameIds);

    List<CartItem> findAllByCartId(Long cartId);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(c.game.id, c.game.price) " +
           "FROM CartItem c WHERE c.cart.userAccount.id = :userAccountId AND c.game.status = :status")
    List<GamePrice> findGamePricesInCartByUserAccountId(Long userAccountId, GameStatus status);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(c.game.id, c.game.price) " +
           "FROM CartItem c WHERE c.cart.id = :cartId AND c.game.status = :status")
    List<GamePrice> findGamePricesInCartByCartId(Long cartId, GameStatus status);

    boolean existsByGameId(Long gameId);

    boolean existsByCartIdAndGameId(Long cartId, Long gameId);
}
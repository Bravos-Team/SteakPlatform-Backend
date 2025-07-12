package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Wishlist;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GamePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    void removeByGameIdAndUserAccountId(Long gameId, Long userAccountId);

    void removeAllByUserAccountId(Long userAccountId);

    List<Wishlist> findAllByUserAccountId(Long userAccountId);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(g.id,g.price)" +
            " FROM Wishlist w JOIN w.game g WHERE w.userAccount.id = :userAccountId AND g.status = :status")
    List<GamePrice> findGamePricesInWishlistByUserAccountId(@Param("userAccountId") Long userAccountId,
                                                            @Param("status") GameStatus status);
}
package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Wishlist;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GamePrice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    void deleteAllByUserAccountId(Long userAccountId);

    List<Wishlist> findAllByUserAccountId(Long userAccountId);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(g.id,g.price)" +
            " FROM Wishlist w JOIN w.game g WHERE w.userAccount.id = :userAccountId AND g.status = :status")
    List<GamePrice> findGamePricesInWishlistByUserAccountId(@Param("userAccountId") Long userAccountId,
                                                            @Param("status") GameStatus status);

    boolean existsByGameIdAndUserAccountId(Long gameId, Long userAccountId);

    void deleteByGameIdAndUserAccountId(Long gameId, Long userAccountId);

    void deleteByUserAccountIdAndGameIdIn(Long userId, List<Long> gameIds);

}
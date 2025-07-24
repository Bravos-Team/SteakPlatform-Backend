package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.GameVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameVersionRepository extends JpaRepository<GameVersion, Long> {

    @Query("SELECT gv FROM GameVersion gv " +
            "WHERE gv.game.id = :gameId and gv.releaseDate <= :current and gv.status = 0 " +
            "ORDER BY gv.releaseDate DESC " +
            "LIMIT 1")
    GameVersion findLatestGameVersionByGameId(@Param("gameId") Long gameId,
                                              @Param("current") Long currentTimeMillis);

}
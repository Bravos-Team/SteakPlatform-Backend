package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.GameVersion;
import com.bravos.steak.store.model.enums.VersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GameVersionRepository extends JpaRepository<GameVersion, Long>, JpaSpecificationExecutor<GameVersion> {

    @Query("SELECT gv FROM GameVersion gv " +
            "WHERE gv.game.id = :gameId and gv.releaseDate <= :current and gv.status = 0 " +
            "ORDER BY gv.releaseDate DESC " +
            "LIMIT 1")
    GameVersion findLatestGameVersionByGameId(@Param("gameId") Long gameId,
                                              @Param("current") Long currentTimeMillis);

    @Query("SELECT gv FROM GameVersion gv " +
            "WHERE gv.game.id = :gameId and gv.releaseDate > :current and gv.status = 0 " +
            "ORDER BY gv.releaseDate ASC " +
            "LIMIT 1")
    GameVersion findNextVersionByGameId(@Param("gameId") Long gameId,
                                        @Param("current") Long currentTimeMillis);

    boolean existsByGameIdAndName(Long gameId, String name);

    List<GameVersion> findAllByGameIdAndStatus(Long gameId, VersionStatus status);

    @Transactional
    @Modifying
    @Query("update GameVersion g set g.status = ?1 where g.game = ?2 and g.status = ?3 and g.releaseDate <= ?4")
    void updateStatusByGameAndStatus(VersionStatus status, Game game, VersionStatus status1, Long currentTimeMillis);

}
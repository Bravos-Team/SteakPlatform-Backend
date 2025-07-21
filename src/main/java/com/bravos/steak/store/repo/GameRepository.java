package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GameIdStatusPrice;
import com.bravos.steak.store.repo.injection.GamePrice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {

    long countGameByIdAndStatus(Long id, GameStatus status);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GameIdStatusPrice(g.id, g.status, g.price) " +
            "FROM Game g WHERE g.id IN :gameIds")
    List<GameIdStatusPrice> findGameIdStatusPrice(@Param("gameIds") Long[] gameIds);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(g.id,g.price) " +
            "FROM Game g WHERE g.id IN :gameIds and g.status = :status")
    List<GamePrice> findGamePricesByIdIn(@Param("gameIds") List<Long> gameIds, @Param("status") GameStatus status);

    List<Game> findByStatusAndCreatedAtLessThanOrderByCreatedAtDesc(GameStatus status, Long cursor);

    @Query("SELECT MAX(g.releaseDate) FROM Game g WHERE g.status = :status")
    Long getMaxCursorByStatus(GameStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"genres", "tags", "publisher"})
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.status = 0 AND g.releaseDate <= :currentTime")
    Optional<Game> findAvailableGameById(@Param("id") Long id, @Param("currentTime") Long currentTime);

}
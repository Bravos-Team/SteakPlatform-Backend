package com.bravos.steak.store.repo;

import com.bravos.steak.administration.model.response.GameListItem;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GameIdStatusPrice;
import com.bravos.steak.store.repo.injection.GamePrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game>, GameRepositoryCustom {

    long countGameByIdAndStatus(Long id, GameStatus status);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GameIdStatusPrice(g.id, g.status, g.price) " +
            "FROM Game g WHERE g.id IN :gameIds")
    List<GameIdStatusPrice> findGameIdStatusPrice(@Param("gameIds") Long[] gameIds);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GamePrice(g.id,g.price) " +
            "FROM Game g WHERE g.id IN :gameIds and g.status = :status")
    List<GamePrice> findGamePricesByIdIn(@Param("gameIds") List<Long> gameIds, @Param("status") GameStatus status);

    List<Game> findByStatusAndCreatedAtLessThanOrderByCreatedAtDesc(GameStatus status, Long cursor);

    @Query("SELECT MAX(g.id) FROM Game g WHERE g.status = 0 AND g.releaseDate <= :current")
    Long getMaxCursorByStatus(Long current);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"genres", "tags", "publisher"})
    @Query("SELECT g FROM Game g WHERE g.id = :id AND (g.status = 0 or g.status = 1)")
    Optional<Game> findAvailableGameById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update Game g set g.name = ?1, g.updatedAt = ?2 where g.id = ?3")
    int updateNameAndUpdatedAtById(String name, Long updatedAt, Long id);

    @Transactional
    @Modifying
    @Query("update Game g set g.status = ?1, g.updatedAt = ?2 where g.id = ?3")
    int updateStatusAndUpdatedAtById(GameStatus status, Long updatedAt, Long id);

    @Transactional
    @Modifying
    @Query("update Game g set g.price = ?1, g.updatedAt = ?2 where g.id = ?3")
    void updatePriceAndUpdatedAtById(BigDecimal price, Long updatedAt, Long id);

    boolean existsByIdAndPublisherId(Long id, Long publisherId);

    List<Game> findAllByPublisherId(Long publisherId, Pageable pageable);

    List<Game> findAllByPublisherIdAndStatus(Long publisherId, GameStatus status, Pageable pageable);

    @Query("SELECT new com.bravos.steak.administration.model.response.GameListItem(g.id, g.name, g.publisher.id, g.publisher.name, g.releaseDate, g.status) " +
            "FROM Game g ")
    Page<GameListItem> getAllGames(Pageable pageable);

    @Query("SELECT new com.bravos.steak.administration.model.response.GameListItem(g.id, g.name, g.publisher.id, g.publisher.name, g.releaseDate, g.status) " +
            "FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<GameListItem> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Game g set g.status = ?1 where g.id = ?2")
    void updateStatusById(GameStatus status, Long id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"genres", "tags"})
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.publisher.id = :publisherId")
    Game findFullDetailsByIdAndPublisherId(@Param("id") Long id, @Param("publisherId") Long publisherId);

    Long countByPublisherIdAndStatus(Long publisherId, GameStatus status);

    Long countByPublisherIdAndStatusNot(Long publisherId, GameStatus status);

    Game findByIdAndPublisherId(Long id, Long publisherId);



}
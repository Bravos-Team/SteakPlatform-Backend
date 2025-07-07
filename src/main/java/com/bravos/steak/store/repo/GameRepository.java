package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.injection.GameIdStatusPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    long countGameByIdAndStatus(Long id, GameStatus status);

    @Query("SELECT new com.bravos.steak.store.repo.injection.GameIdStatusPrice(g.id, g.status, g.price) " +
           "FROM Game g WHERE g.id IN :gameIds")
    List<GameIdStatusPrice> findGameIdStatusPrice(@Param("gameIds") Long[] gameIds);


}
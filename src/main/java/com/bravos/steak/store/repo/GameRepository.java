package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    long countGameByIdAndStatus(Long id, GameStatus status);
}
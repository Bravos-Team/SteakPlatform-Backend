package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
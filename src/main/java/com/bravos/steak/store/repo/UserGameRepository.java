package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
}
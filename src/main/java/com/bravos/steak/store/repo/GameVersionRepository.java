package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.GameVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameVersionRepository extends JpaRepository<GameVersion, Long> {
}
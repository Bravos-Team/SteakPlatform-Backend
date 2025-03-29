package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game_Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGenreRepository extends JpaRepository<Game_Genre, Long> {
}

package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Game_Genre;
import com.bravos.steak.store.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Page<Game> findByNameContainingIgnoreCase(String name,
                                              Pageable pageable);

    @Query("SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre ge WHERE ge.slug IN :slugs")
    Page<Game> findByGamesByGenreSlugs(List<String> slugs,
                                       Pageable pageable);

    @Query("SELECT g FROM Game g JOIN g.publisher p WHERE p.name = :publisherName")
    Page<Game> findByPublisher(String publisherName,
                               Pageable pageable);

}

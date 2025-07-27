package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
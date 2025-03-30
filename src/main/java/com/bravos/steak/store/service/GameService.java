package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.GameResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {

    Page<GameResponse> findAll(Pageable pageable);

    Page<GameResponse> findByName(String name, Pageable pageable);

    Page<GameResponse> findGameBySlugs(List<String> slugs, Pageable pageable);

    GameResponse findById(Long id);

}

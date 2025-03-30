package com.bravos.steak.store.service.impl;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.mapper.GameMapper;
import com.bravos.steak.store.model.response.GameResponse;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    @Override
    public Page<GameResponse> findAll(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(gameMapper::toGameResponse);
    }

    @Override
    public Page<GameResponse> findByName(String name, Pageable pageable) {
        return gameRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(gameMapper::toGameResponse);
    }

    @Override
    public Page<GameResponse> findGameBySlugs(List<String> slugs, Pageable pageable) {
        if (slugs == null || slugs.isEmpty()) {
            throw new IllegalArgumentException("Genre slugs cannot be empty");
        }
        return gameRepository.findByGamesByGenreSlugs(slugs, pageable).map(gameMapper::toGameResponse);
    }

    @Override
    public GameResponse findById(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new RuntimeException("Game not found"));
        return gameMapper.toGameResponse(game);
    }

    // No usage in current
    /*private GameResponse getGameResponse(Game game) {
        GameResponse gameResponse = new GameResponse();
        gameResponse.setPublisherName(game.getPublisher().getName());
        gameResponse.setGenres(gameMapper.mapGenres(game.getGameGenres()));
        return gameResponse;
    }*/
}

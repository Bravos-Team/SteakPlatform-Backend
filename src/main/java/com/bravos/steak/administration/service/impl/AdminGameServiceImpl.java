package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.model.response.GameListItem;
import com.bravos.steak.administration.service.AdminGameService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminGameServiceImpl implements AdminGameService {
    private final GameRepository gameRepository;

    public AdminGameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Page<GameListItem> getAllGames(int page, int size) {
        return gameRepository.getAllGames(PageRequest.of(page, size));
    }

    @Override
    public Page<GameListItem> searchGames(String query, int page, int size) {
        return gameRepository.findByNameContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    @Override
    @Transactional
    public void updateGameStatus(Long gameId, String status) {
        GameStatus gameStatus;
        try {
            gameStatus = GameStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid game status: " + status);
        }
        gameRepository.updateStatusById(gameStatus,gameId);
    }
}

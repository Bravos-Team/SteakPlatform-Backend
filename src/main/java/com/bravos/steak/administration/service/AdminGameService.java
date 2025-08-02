package com.bravos.steak.administration.service;

import com.bravos.steak.administration.model.response.GameListItem;
import org.springframework.data.domain.Page;

public interface AdminGameService {

    Page<GameListItem> getAllGames(int page, int size);

    Page<GameListItem> searchGames(String query, int page, int size);

    void updateGameStatus(Long gameId, String status);

}

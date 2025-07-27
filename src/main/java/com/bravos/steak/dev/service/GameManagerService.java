package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.model.response.PublisherGameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;

import java.util.List;

public interface GameManagerService {

    GameStoreDetail updateGameDetails(UpdateGameDetailsRequest request);

    void updateGameStatus(Long gameId, String status);

    void updateGamePrice(Long gameId, Double price);

    List<PublisherGameListItem> listAllGames(int page, int size, String status);

}

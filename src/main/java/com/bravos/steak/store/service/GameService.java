package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.GameListResponse;

public interface GameService {
    GameListResponse getGameStoreList(Long cursor, int pageSize);
}

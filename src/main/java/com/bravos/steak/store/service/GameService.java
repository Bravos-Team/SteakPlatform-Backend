package com.bravos.steak.store.service;

import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.DownloadResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;

public interface GameService {

    CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize);

    CursorResponse<GameListItem> getFilteredGames(
            Long cursor,
            Long minPrice,
            Long maxPrice,
            int pageSize
    );

    GameStoreDetail getGameStoreDetails(Long gameId);

    DownloadResponse getGameDownloadUrl(Long gameId);

}

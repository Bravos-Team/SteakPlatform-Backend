package com.bravos.steak.store.service;

import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.request.FilterQuery;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.DownloadResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;

import java.util.Set;

public interface GameService {

    CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize);

    CursorResponse<GameListItem> getFilteredGames(
            FilterQuery filterQuery
    );

    GameStoreDetail getGameStoreDetails(Long gameId);

    DownloadResponse getGameDownloadUrl(Long gameId);

    Set<Genre> getAllGenres();

    Set<Tag> getAllTags();

    GameStoreDetail invalidateAndGetGameStoreDetails(Long gameId);

}

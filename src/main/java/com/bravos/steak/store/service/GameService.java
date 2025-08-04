package com.bravos.steak.store.service;

import com.bravos.steak.common.model.CustomPage;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.request.FilterQuery;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.DownloadResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface GameService {

    CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize);

    CustomPage<GameListItem> getFilteredGames(FilterQuery filterQuery);

    CustomPage<GameListItem> getNewestGames(int page, int pageSize);

    CustomPage<GameListItem> getComingSoonGames(int page, int pageSize);

    GameStoreDetail getGameStoreDetails(Long gameId);

    DownloadResponse getGameDownloadUrl(Long gameId);

    Set<Genre> getAllGenres();

    Set<Tag> getAllTags();

    GameStoreDetail invalidateAndGetGameStoreDetails(Long gameId);

}

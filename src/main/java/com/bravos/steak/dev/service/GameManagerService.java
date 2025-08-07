package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.CreateNewVersionRequest;
import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.model.request.UpdateVersionRequest;
import com.bravos.steak.dev.model.response.GameVersionListItem;
import com.bravos.steak.dev.model.response.PublisherGameListItem;
import com.bravos.steak.store.model.response.FullGameDetails;
import com.bravos.steak.store.model.response.GameStoreDetail;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GameManagerService {

    GameStoreDetail updateGameDetails(UpdateGameDetailsRequest request);

    void updateGameStatus(Long gameId, String status);

    void updateGamePrice(Long gameId, Double price);

    Page<PublisherGameListItem> listAllGames(int page, int size, String status);

    void createNewVersion(CreateNewVersionRequest request);

    void updateDraftVersion(UpdateVersionRequest request);

    void deleteDraftVersion(Long gameId, Long versionId);

    void markAsLatestStableNow(Long gameId, Long versionId);

    List<GameVersionListItem> getGameVersions(Long gameId);

    FullGameDetails getFullGameDetails(Long gameId);

    Long countGamesByStatus(String status);

}

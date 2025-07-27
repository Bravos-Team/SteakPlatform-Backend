package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;

import java.util.List;

public interface GameManagerService {

    void updateGameDetails(UpdateGameDetailsRequest request);

    void updateGameStatus(Long gameId, String status);

    void updateGamePrice(Long gameId, Double price);

    List<Genre> getAllGenres();

    List<Tag> getAllTags();

}

package com.bravos.steak.store.service;

import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.model.response.GameLibraryItem;
import com.bravos.steak.store.model.response.TimePlayResponse;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface LibraryService {

    List<GameLibraryItem> getMyLibrary(Sort sort);

    TimePlayResponse getTimesPlayed(Long gameId);
}

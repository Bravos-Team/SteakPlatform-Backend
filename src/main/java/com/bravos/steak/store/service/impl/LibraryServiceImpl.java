package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.store.model.response.GameLibraryItem;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.store.service.LibraryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final SessionService sessionService;
    private final UserGameRepository userGameRepository;
    private final GameDetailsRepository gameDetailsRepository;

    public LibraryServiceImpl(SessionService sessionService, UserGameRepository userGameRepository, GameDetailsRepository gameDetailsRepository) {
        this.sessionService = sessionService;
        this.userGameRepository = userGameRepository;
        this.gameDetailsRepository = gameDetailsRepository;
    }

    @Override
    public List<GameLibraryItem> getMyLibrary() {
        Long userId = (Long) sessionService.getAuthentication().getPrincipal();
        var gameIdTitles = userGameRepository.findGameIdTitleByUserId(userId);
        List<GameLibraryItem> gameLibraryItems = new ArrayList<>(gameIdTitles.size());

        return null;
    }

}

package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.entity.details.GameDetails;
import com.bravos.steak.store.model.response.GameLibraryItem;
import com.bravos.steak.store.model.response.TimePlayResponse;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.store.repo.injection.LibraryInfo;
import com.bravos.steak.store.service.LibraryService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final SessionService sessionService;
    private final UserGameRepository userGameRepository;
    private final GameDetailsRepository gameDetailsRepository;

    public LibraryServiceImpl(SessionService sessionService, UserGameRepository userGameRepository,
                              GameDetailsRepository gameDetailsRepository) {
        this.sessionService = sessionService;
        this.userGameRepository = userGameRepository;
        this.gameDetailsRepository = gameDetailsRepository;
    }

    @Override
    public List<GameLibraryItem> getMyLibrary(Sort sort) {
        Long userId = (Long) sessionService.getAuthentication().getPrincipal();
        if(sort == null) {
            sort = Sort.by(Sort.Direction.DESC, "ownedDate");
        }
        List<LibraryInfo> libraryInfos = userGameRepository.findLibraryInfoByUserId(userId,sort);
        if(libraryInfos.isEmpty()) {
            return List.of();
        }
        List<GameDetails> gameDetailsList = gameDetailsRepository.findForLibraryByIdIn(libraryInfos.stream()
                .map(LibraryInfo::getGameId)
                .toList());
        Map<Long,GameLibraryItem> libraryMap = new HashMap<>(libraryInfos.size());
        libraryInfos.forEach(l -> libraryMap.put(l.getGameId(),
                GameLibraryItem.builder()
                        .gameId(l.getGameId())
                        .ownedDate(l.getOwnedDate())
                        .lastPlayedAt(l.getLastPlayedAt())
                        .playSeconds(l.getPlaySeconds())
                        .build()));
        gameDetailsList.forEach(detail -> {
            GameLibraryItem item = libraryMap.get(detail.getId());
            item.setTitle(detail.getTitle());
            item.setThumbnailUrl(detail.getThumbnail());
        });
        return libraryMap.values().stream().toList();
    }

    @Override
    public TimePlayResponse getTimesPlayed(Long gameId) {
        Long userId = (Long) sessionService.getAuthentication().getPrincipal();
        TimePlayResponse response = userGameRepository.findTimePlayedByGameIdAndUserId(gameId, userId);
        if(response == null) throw new BadRequestException("You don't own this game");
        return response;
    }

}

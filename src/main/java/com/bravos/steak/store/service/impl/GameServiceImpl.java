package com.bravos.steak.store.service.impl;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.details.GameDetails;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameListResponse;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.service.GameService;
import com.bravos.steak.store.specifications.GameSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    GameRepository gameRepository;
    GameDetailsRepository detailsRepository;
    private final GameDetailsRepository gameDetailsRepository;

    @Override
    public GameListResponse getGameStoreList(Long cursor, int pageSize) {

        if (pageSize == 0) pageSize = 10;

        if (cursor == null) return GameListResponse.builder().build();
        List<Game> games = gameRepository.findAll();
        if (games.isEmpty()) return GameListResponse.builder().build();
        List<GameDetails> details = detailsRepository.findAll();
        if (details.isEmpty()) return GameListResponse.builder().build();
        return null;
    }

    public GameListResponse getFilteredGames(
            Long cursor,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            GameStatus status,
            List<String> platforms, // dùng sau nếu mày join
            int pageSize
    ) {
        if (pageSize == 0) pageSize = 10;
        Specification<Game> spec = GameSpecification.withFilters(status, minPrice, maxPrice, platforms, cursor);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Game> games = gameRepository.findAll(spec, sort);

        if (games.isEmpty()) return GameListResponse.builder().build();

        List<Long> gameIds = games.stream().map(Game::getId).toList();
        List<GameDetails> details = gameDetailsRepository.findForLibraryByIdIn(gameIds);

        List<GameListItem> items = games.stream().map(g -> {
            GameDetails detail = details.stream().filter(d -> d.getId().equals(g.getId())).findFirst().orElse(null);
            if (detail == null) return null;
            return GameListItem.builder()
                    .id(g.getId())
                    .name(detail.getTitle())
                    .thumbnail(detail.getThumbnail())
                    .price(g.getPrice())
                    .createAt(g.getCreatedAt())
                    .updatedAt(g.getUpdatedAt())
                    .releaseDate(g.getReleaseDate())
                    .build();
        }).filter(Objects::nonNull).toList();

        Long nextCursor = items.isEmpty() ? null : items.getLast().getCreateAt();

        return GameListResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .build();
    }
}

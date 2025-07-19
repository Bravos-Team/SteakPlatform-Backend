package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import com.bravos.steak.store.service.GameService;
import com.bravos.steak.store.specifications.GameSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    GameRepository gameRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final RedisService redisService;

    @Override
    public CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize) {
        Specification<Game> spec = GameSpecification.withoutFilters(cursor);
        List<Game> games = gameRepository.findAll(spec, PageRequest.of(0, pageSize)).getContent();
        if (games.isEmpty()) return CursorResponse.empty();
        List<CartGameInfo> gameDetails = gameDetailsRepository.findByIdIn(games.stream().map(Game::getId).toList());
        Map<Long,GameListItem> gameListItemMap = new LinkedHashMap<>(games.size());
        games.forEach(game -> gameListItemMap.put(game.getId(),
                GameListItem.builder()
                        .id(game.getId())
                        .name(game.getName())
                        .price(game.getPrice().doubleValue())
                        .releaseDate(game.getReleaseDate())
                        .build()));
        gameDetails.forEach(detail -> {
            GameListItem item = gameListItemMap.get(detail.getId());
            item.setThumbnail(detail.getThumbnail());
        });

        Long maxCursor = getMaxCursorWithoutFilters();
        Long currentCursor = games.getLast().getReleaseDate();

        if(maxCursor < currentCursor) {
            redisService.delete("cursor:non-filter");
            maxCursor = getMaxCursorWithoutFilters();
        }

        boolean hasNextCursor = maxCursor != null && maxCursor > currentCursor;
        return CursorResponse.<GameListItem>builder()
                .items(gameListItemMap.values().stream().toList())
                .maxCursor(maxCursor)
                .hasNextCursor(hasNextCursor)
                .build();
    }

    @Override
    public CursorResponse<GameListItem> getFilteredGames(
            Long cursor,
            Long minPrice,
            Long maxPrice,
            int pageSize
    ) {
        if (pageSize == 0) pageSize = 10;

        Specification<Game> spec = GameSpecification.withFilters(minPrice, maxPrice, cursor);
        List<Game> games = gameRepository.findAll(spec,PageRequest.of(0,pageSize)).getContent();
        if (games.isEmpty()) return CursorResponse.empty();

        long maxCursor = gameRepository.getMaxCursorByStatus(GameStatus.OPENING);

        return null;
    }

    private Long getMaxCursorWithoutFilters() {
        String key = "cursor:non-filter";
        Long maxCursor = redisService.get(key, Long.class);
        if (maxCursor != null) return maxCursor;
        maxCursor = gameRepository.getMaxCursorByStatus(GameStatus.OPENING);
        redisService.save(key,maxCursor,10, TimeUnit.MINUTES);
        return maxCursor;
    }

    private Long getMaxCursorWithFilters(Long minPrice, Long maxPrice) {
        int hashCode = Objects.hash(minPrice, maxPrice);
        String key = "cursor:filter:" + hashCode;
        Long maxCursor = redisService.get(key, Long.class);
        if (maxCursor != null) return maxCursor;

        redisService.save(key,maxCursor,10, TimeUnit.MINUTES);
        return maxCursor;
    }

}

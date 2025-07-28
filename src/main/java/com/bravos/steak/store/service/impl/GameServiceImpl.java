package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.GameVersion;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.entity.details.GameDetails;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.DownloadResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;
import com.bravos.steak.store.repo.*;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import com.bravos.steak.store.service.DownloadGameService;
import com.bravos.steak.store.service.GameService;
import com.bravos.steak.store.specifications.GameSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    GameRepository gameRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final RedisService redisService;
    private final UserGameRepository userGameRepository;
    private final SessionService sessionService;
    private final GameVersionRepository gameVersionRepository;
    private final DownloadGameService downloadGameService;
    private final ObjectMapper objectMapper;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;

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

    @Override
    public GameStoreDetail getGameStoreDetails(Long gameId) {
        String key = "game:store:detail:" + gameId;
        GameStoreDetail cachedDetail = redisService.get(key, GameStoreDetail.class);
        if(cachedDetail != null) {
            return getGameStoreDetailWithOwnedStatus(gameId, cachedDetail);
        }
        RedisCacheEntry<GameStoreDetail> cacheEntry = RedisCacheEntry.<GameStoreDetail>builder()
                .key(key)
                .fallBackFunction(() -> getGameStoreDetailFromDb(gameId))
                .keyTimeout(10)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(100)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        GameStoreDetail gameStoreDetail = redisService.getWithLock(cacheEntry, GameStoreDetail.class);
        return getGameStoreDetailWithOwnedStatus(gameId, gameStoreDetail);
    }

    @Override
    public DownloadResponse getGameDownloadUrl(Long gameId) {
        Long userId = sessionService.getCurrentUserId();
        if(userId == null) {
            throw new UnauthorizeException("You must be logged in to download games");
        }
        if(!userGameRepository.existsByGameIdAndUserId(gameId,userId)) {
            throw new ForbiddenException("You do not own this game, cannot download");
        }
        GameVersion gameVersion = gameVersionRepository
                .findLatestGameVersionByGameId(gameId,DateTimeHelper.currentTimeMillis());

        if(gameVersion == null) {
            throw new ResourceNotFoundException("Game version not found or not available");
        }
        String downloadUrl = gameVersion.getDownloadUrl();
        if(downloadUrl == null || downloadUrl.isEmpty()) {
            throw new ResourceNotFoundException("Download URL not available for this game version");
        }
        downloadUrl = downloadGameService.getGameDownloadUrl(downloadUrl,sessionService.getUserIpAddress());
        return DownloadResponse.builder()
                .fileName(gameId + "-" + gameVersion.getName() + ".tar.zst")
                .downloadUrl(downloadUrl)
                .fileSize(gameVersion.getFileSize())
                .installSize(gameVersion.getInstallSize())
                .execPath(gameVersion.getExecPath())
                .checksum(gameVersion.getChecksum())
                .build();
    }

    @Override
    public Set<Genre> getAllGenres() {
        RedisCacheEntry<Collection<Genre>> cacheEntry = RedisCacheEntry.<Collection<Genre>>builder()
                .key("allGenres")
                .fallBackFunction(this::getAllGenresFromDatabase)
                .keyTimeout(15)
                .keyTimeUnit(TimeUnit.MINUTES)
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Genre.class);
        return new HashSet<>(redisService.getWithLock(cacheEntry, type, Genre.class));
    }

    @Override
    public Set<Tag> getAllTags() {
        RedisCacheEntry<Collection<Tag>> cacheEntry = RedisCacheEntry.<Collection<Tag>>builder()
                .key("allTags")
                .fallBackFunction(this::getAllTagsFromDatabase)
                .keyTimeout(15)
                .keyTimeUnit(TimeUnit.MINUTES)
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Tag.class);
        return new HashSet<>(redisService.getWithLock(cacheEntry, type, Tag.class));
    }

    @Override
    public GameStoreDetail invalidateAndGetGameStoreDetails(Long gameId) {
        String key = "game:store:detail:" + gameId;
        redisService.delete(key);
        return getGameStoreDetails(gameId);
    }

    private List<Genre> getAllGenresFromDatabase() {
        try {
            return genreRepository.findAll();
        } catch (Exception e) {
            log.error("Failed to fetch genres from database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch genres from database", e);
        }
    }

    private List<Tag> getAllTagsFromDatabase() {
        try {
            return tagRepository.findAll();
        } catch (Exception e) {
            log.error("Failed to fetch tags from database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch tags from database", e);
        }
    }


    private GameStoreDetail getGameStoreDetailWithOwnedStatus(Long gameId, GameStoreDetail cachedDetail) {
        if(cachedDetail.getDetails() == null) {
            throw new ResourceNotFoundException("Game not found or not available");
        }
        Long userId = sessionService.getCurrentUserId();
        boolean isGameOwned = userId != null && userGameRepository.existsByGameIdAndUserId(gameId, userId);
        cachedDetail.setIsOwned(isGameOwned);
        return cachedDetail;
    }

    private GameStoreDetail getGameStoreDetailFromDb(Long gameId) {
        Game game = gameRepository.findAvailableGameById(gameId,
                DateTimeHelper.currentTimeMillis()).orElse(null);

        if(game == null) {
            return GameStoreDetail.builder().build();
        }

        GameDetails gameDetails = gameDetailsRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game details not found"));

        return GameStoreDetail.builder()
                .details(gameDetails)
                .price(game.getPrice().doubleValue())
                .publisherName(game.getPublisher().getName())
                .tags(game.getTags().stream().toList())
                .genres(game.getGenres().stream().toList())
                .build();
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

package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.model.CustomPage;
import com.bravos.steak.common.model.CustomPageInfo;
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
import com.bravos.steak.store.model.request.FilterQuery;
import com.bravos.steak.store.model.response.CursorResponse;
import com.bravos.steak.store.model.response.DownloadResponse;
import com.bravos.steak.store.model.response.GameListItem;
import com.bravos.steak.store.model.response.GameStoreDetail;
import com.bravos.steak.store.repo.*;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import com.bravos.steak.store.service.DownloadGameService;
import com.bravos.steak.store.service.GameService;
import com.bravos.steak.store.service.UserGameService;
import com.bravos.steak.store.specifications.GameSpecification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final GameRepository gameRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final RedisService redisService;
    private final UserGameRepository userGameRepository;
    private final SessionService sessionService;
    private final GameVersionRepository gameVersionRepository;
    private final DownloadGameService downloadGameService;
    private final ObjectMapper objectMapper;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final UserGameService userGameService;

    @Override
    public CursorResponse<GameListItem> getGameStoreList(Long cursor, int pageSize) {
        String key = "game:store:list:" + cursor + ":" + pageSize;
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getGamesFromDb(cursor, pageSize))
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(30000)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    @Override
    public CustomPage<GameListItem> getFilteredGames(FilterQuery filterQuery) {
        if(filterQuery.getMinPrice() != null && filterQuery.getMaxPrice() != null
                && filterQuery.getMinPrice() > filterQuery.getMaxPrice()) {
            double temp = filterQuery.getMinPrice();
            filterQuery.setMinPrice(filterQuery.getMaxPrice());
            filterQuery.setMaxPrice(temp);
        }
        int hashCode = filterQuery.hashCode();

        String key = "game:filter:" + hashCode;
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getFilteredGamesFromDb(filterQuery))
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(2000)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    @Override
    public CustomPage<GameListItem> getNewestGames(int page, int pageSize) {
        String key = "game:newest:" + page + ":" + pageSize;
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getNewestGamesFromDb(page, pageSize))
                .keyTimeout(1)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(1000)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    private CustomPage<GameListItem> getNewestGamesFromDb(int page, int pageSize) {
        Specification<Game> spec = GameSpecification.newestGames();
        return new CustomPage<>(getGameListItemsBySpec(spec, page, pageSize));
    }

    @Override
    public CustomPage<GameListItem> getComingSoonGames(int page, int pageSize) {
        String key = "game:coming-soon:" + page + ":" + pageSize;
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getComingSoonGamesFromDb(page, pageSize))
                .keyTimeout(1)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(1000)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    @Override
    public CustomPage<GameListItem> getTopMostPlayedGames(int page, int pageSize) {
        String key = "game:top-most-played:" + page + ":" + pageSize;
        RedisCacheEntry<Object> cacheEntry = RedisCacheEntry.builder()
                .key(key)
                .fallBackFunction(() -> getTopMostPlayedGamesFromDb(page, pageSize))
                .keyTimeout(5)
                .keyTimeUnit(TimeUnit.MINUTES)
                .lockTimeout(1000)
                .lockTimeUnit(TimeUnit.MILLISECONDS)
                .retryTime(3)
                .build();
        Object value = redisService.getWithLock(cacheEntry, Object.class);
        return objectMapper.convertValue(value, new TypeReference<>() {});
    }

    private CustomPage<GameListItem> getTopMostPlayedGamesFromDb(int page, int pageSize) {
        long start = (long) (page - 1) * pageSize;
        long end = start + pageSize - 1;
        Set<Long> gameIds = userGameService.getTopPlayedGames(start, end);
        Specification<Game> spec = GameSpecification.topMostPlayedGames(gameIds);
        return CustomPage.<GameListItem>builder()
                .content(getGameListItemsBySpec(spec, page, pageSize).getContent())
                .page(CustomPageInfo.builder()
                        .number(page - 1)
                        .size(pageSize)
                        .totalPages(Math.toIntExact(userGameService.countTotalPlayedGames()) / pageSize + 1)
                        .totalElements(gameIds.size())
                        .build())
                .build();
    }

    private CustomPage<GameListItem> getComingSoonGamesFromDb(int page, int pageSize) {
        Specification<Game> spec = GameSpecification.comingSoonGames();
        return new CustomPage<>(getGameListItemsBySpec(spec, page, pageSize));         
    }

    private CursorResponse<GameListItem> getGamesFromDb(Long cursor, int pageSize) {
        try {
            Specification<Game> spec = GameSpecification.withoutFilters(cursor);
            List<Game> games = new ArrayList<>(gameRepository.findAll(spec, PageRequest.of(0, pageSize)).getContent());
            games.sort(Comparator.comparing(Game::getReleaseDate).reversed());
            if (games.isEmpty()) return CursorResponse.empty();
            List<CartGameInfo> gameDetails = gameDetailsRepository.findByIdIn(games.stream().map(Game::getId).toList());
            Map<Long,GameListItem> gameListItemMap = getGameListItemMap(games, gameDetails);
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
        } catch (Exception e) {
            log.error("Failed to fetch game list from database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch game list from database", e);
        }
    }

    private CustomPage<GameListItem> getFilteredGamesFromDb(FilterQuery filterQuery) {
        Specification<Game> spec = GameSpecification.withFilters(filterQuery);
        return new CustomPage<>(getGameListItemsBySpec(spec, filterQuery.getPage(), filterQuery.getPageSize()));
    }

    private Page<GameListItem> getGameListItemsBySpec(Specification<Game> spec, int page, int pageSize) {
        Page<Game> gamePage = gameRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        List<Game> games = gamePage.getContent();
        if(games.isEmpty()) return Page.empty();
        List<CartGameInfo> gameDetails = gameDetailsRepository.findByIdIn(games.stream().map(Game::getId).toList());
        Map<Long,GameListItem> gameListItemMap = getGameListItemMap(games, gameDetails);
        return gamePage.map(game -> {
            GameListItem item = gameListItemMap.get(game.getId());
            return GameListItem.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice())
                    .releaseDate(item.getReleaseDate())
                    .thumbnail(item.getThumbnail())
                    .build();
        });
    }

    private Map<Long,GameListItem> getGameListItemMap(List<Game> games, List<CartGameInfo> gameDetails) {
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
        return gameListItemMap;
    }



    @Override
    public GameStoreDetail getGameStoreDetails(Long gameId) {
        String key = "game:store:detail:" + gameId;
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
        RedisCacheEntry<Set<Genre>> cacheEntry = RedisCacheEntry.<Set<Genre>>builder()
                .key("allGenres")
                .fallBackFunction(this::getAllGenresFromDatabase)
                .keyTimeout(15)
                .keyTimeUnit(TimeUnit.MINUTES)
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Genre.class);
        return redisService.getWithLock(cacheEntry,type);
    }

    @Override
    public Set<Tag> getAllTags() {
        RedisCacheEntry<Set<Tag>> cacheEntry = RedisCacheEntry.<Set<Tag>>builder()
                .key("allTags")
                .fallBackFunction(this::getAllTagsFromDatabase)
                .keyTimeout(15)
                .keyTimeUnit(TimeUnit.MINUTES)
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Tag.class);
        return redisService.getWithLock(cacheEntry, type);
    }

    @Override
    public GameStoreDetail invalidateAndGetGameStoreDetails(Long gameId) {
        String key = "game:store:detail:" + gameId;
        redisService.delete(key);
        return getGameStoreDetails(gameId);
    }

    private Set<Genre> getAllGenresFromDatabase() {
        try {
            return new HashSet<>(genreRepository.findAll());
        } catch (Exception e) {
            log.error("Failed to fetch genres from database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch genres from database", e);
        }
    }

    private Set<Tag> getAllTagsFromDatabase() {
        try {
            return new HashSet<>(tagRepository.findAll());
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
        Game game = gameRepository.findById(gameId).orElse(null);

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

}

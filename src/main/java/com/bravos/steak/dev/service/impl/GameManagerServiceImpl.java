package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.model.GameThumbnail;
import com.bravos.steak.dev.model.request.CreateNewVersionRequest;
import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.model.request.UpdateVersionRequest;
import com.bravos.steak.dev.model.response.GameVersionListItem;
import com.bravos.steak.dev.model.response.PublisherGameListItem;
import com.bravos.steak.dev.service.GameManagerService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ForbiddenException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.GameVersion;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.enums.VersionStatus;
import com.bravos.steak.store.model.response.GameStoreDetail;
import com.bravos.steak.store.repo.*;
import com.bravos.steak.store.service.GameService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class GameManagerServiceImpl implements GameManagerService {

    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate;
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final SessionService sessionService;
    private final GameService gameService;
    private final GameDetailsRepository gameDetailsRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final GameVersionRepository gameVersionRepository;

    public GameManagerServiceImpl(ObjectMapper objectMapper, MongoTemplate mongoTemplate, GameRepository gameRepository,
                                  GenreRepository genreRepository, TagRepository tagRepository, SessionService sessionService,
                                  GameService gameService, GameDetailsRepository gameDetailsRepository, SnowflakeGenerator snowflakeGenerator,
                                  GameVersionRepository gameVersionRepository) {
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.tagRepository = tagRepository;
        this.sessionService = sessionService;
        this.gameService = gameService;
        this.gameDetailsRepository = gameDetailsRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.gameVersionRepository = gameVersionRepository;
    }

    @Override
    @Transactional
    public GameStoreDetail updateGameDetails(UpdateGameDetailsRequest request) {
        Map<String, Object> changedData = objectMapper.convertValue(request, new TypeReference<>() {
        });
        Long gameId = (Long) changedData.remove("gameId");

        if (!isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + request.getGameId() + " does not exist or is not owned by the publisher.");
        }

        Set<Integer> genreIds = (Set<Integer>) changedData.remove("genres");
        Set<Integer> tagIds = (Set<Integer>) changedData.remove("tags");

        Update update = new Update();
        changedData.forEach((key, value) -> {
            if (value != null) {
                update.set(key, value);
            }
        });

        update.set("updatedAt", DateTimeHelper.currentTimeMillis());

        try {
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(gameId)), update, "gameDetails");
        } catch (Exception e) {
            log.error("Failed to update game details for game ID {}: {}", gameId, e.getMessage(), e);
            throw new RuntimeException("Failed to update game details for game ID " + gameId, e);
        }

        Game game = null;
        if (genreIds != null && !genreIds.isEmpty()) {
            game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new BadRequestException("Game with ID " + gameId + " does not exist."));
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));
            game.setGenres(genres);
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            if (game == null) {
                game = gameRepository.findById(gameId)
                        .orElseThrow(() -> new BadRequestException("Game with ID " + gameId + " does not exist."));
            }
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
            game.setTags(tags);
        }
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            if (game == null) {

                try {
                    gameRepository.updateNameAndUpdatedAtById(
                            request.getTitle(), DateTimeHelper.currentTimeMillis(), gameId
                    );
                } catch (Exception e) {
                    throw new RuntimeException("Failed to update game name for game ID " + gameId, e);
                }

            } else {
                game.setName(request.getTitle());
            }
        }
        if (game != null) {

            game.setUpdatedAt(DateTimeHelper.currentTimeMillis());

            try {
                gameRepository.saveAndFlush(game);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save game with ID " + gameId, e);
            }

        }
        return gameService.invalidateAndGetGameStoreDetails(gameId);
    }

    @Override
    @Transactional
    public void updateGameStatus(Long gameId, String status) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BadRequestException("Game with ID " + gameId + " does not exist."));
        long publisherId = getPublisherIdFromClaims();
        if(game.getPublisher().getId() != publisherId) {
            throw new BadRequestException("Game with ID " + gameId + " is not owned by the publisher.");
        }
        try {
            if(game.getStatus() == GameStatus.valueOf(status)) return;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid game status: " + status);
        }
        if(game.getStatus() == GameStatus.DELETED) {
            throw new BadRequestException("Game with ID " + gameId + " is deleted and cannot be updated.");
        }
        if(game.getStatus() == GameStatus.BANNED) {
            throw new ForbiddenException("Game with ID " + gameId + " is banned and cannot be updated.");
        }
        game.setStatus(GameStatus.valueOf(status));
        game.setUpdatedAt(DateTimeHelper.currentTimeMillis());
        try {
            gameRepository.saveAndFlush(game);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game status for game ID " + gameId, e);
        }
    }

    @Override
    @Transactional
    public void updateGamePrice(Long gameId, Double price) {
        if (price == null || price < 0 || price.isNaN() || price.isInfinite()) {
            throw new BadRequestException("Price must be a positive number.");
        }
        if (!isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }
        try {
            gameRepository.updatePriceAndUpdatedAtById(BigDecimal.valueOf(price), DateTimeHelper.currentTimeMillis(), gameId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game price for game ID " + gameId, e);
        }
    }

    @Override
    public List<PublisherGameListItem> listAllGames(int page, int size, String status) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Page must be >= 0 and size must be > 0.");
        }
        long publisherId = getPublisherIdFromClaims();
        List<Game> games;
        if (status == null || status.isBlank() || status.equalsIgnoreCase("all")) {
            games = gameRepository.findAllByPublisherId(publisherId, PageRequest.of(page, size));
        } else {
            GameStatus gameStatus;
            try {
                gameStatus = GameStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid game status: " + status);
            }
            games = gameRepository.findAllByPublisherIdAndStatus(publisherId, gameStatus, PageRequest.of(page, size));
        }

        if (games.isEmpty()) return List.of();

        List<GameThumbnail> gameThumbnails = gameDetailsRepository.findThumbnailsByIdIn(games.stream().map(Game::getId).toList());
        Map<Long, PublisherGameListItem> gameMap = new HashMap<>(games.size());
        games.forEach(game -> gameMap.put(game.getId(), PublisherGameListItem.builder()
                .gameId(game.getId())
                .title(game.getName())
                .status(game.getStatus())
                .build()));
        gameThumbnails.forEach(thumbnail -> {
            PublisherGameListItem item = gameMap.get(thumbnail.getId());
            item.setThumbnail(thumbnail.getThumbnail());
        });

        return gameMap.values().stream().toList();
    }

    @Override
    @Transactional
    public void createNewVersion(CreateNewVersionRequest request) {
        if(!isGameOwnedByPublisher(request.getGameId())) {
            throw new BadRequestException("Game with ID " + request.getGameId() + " does not exist or is not owned by the publisher.");
        }
        if(gameVersionRepository.existsByGameIdAndName(request.getGameId(), request.getVersionName())) {
            throw new BadRequestException("Version with name " + request.getVersionName() + " already exists for game ID " + request.getGameId());
        }
        GameVersion gameVersion = GameVersion.builder()
                .id(snowflakeGenerator.generateId())
                .game(Game.builder().id(request.getGameId()).build())
                .name(request.getVersionName())
                .changeLog(request.getChangeLog())
                .execPath(request.getExecPath())
                .downloadUrl(request.getDownloadUrl())
                .status(request.getIsReady() ? VersionStatus.STABLE : VersionStatus.DRAFT)
                .releaseDate(request.getReleaseDate())
                .fileSize(request.getFileSize())
                .installSize(request.getInstallSize())
                .checksum(request.getChecksum())
                .createdAt(DateTimeHelper.currentTimeMillis())
                .updatedAt(DateTimeHelper.currentTimeMillis())
                .build();

        try {
            gameVersionRepository.saveAndFlush(gameVersion);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new version for game ID " + request.getGameId(), e);
        }
    }

    @Override
    @Transactional
    public void updateDraftVersion(UpdateVersionRequest request) {
        if (!isGameOwnedByPublisher(request.getGameId())) {
            throw new BadRequestException("Game with ID " + request.getGameId() + " does not exist or is not owned by the publisher.");
        }
        GameVersion gameVersion = gameVersionRepository.findById(request.getVersionId())
                .orElseThrow(() -> new BadRequestException("Version with ID " + request.getVersionId() + " does not exist."));

        if(!Objects.equals(gameVersion.getGame().getId(), request.getGameId())) {
            throw new BadRequestException("Version with ID " + request.getVersionId() + " does not belong to game ID " + request.getGameId());
        }

        if (gameVersion.getStatus() != VersionStatus.DRAFT) {
            throw new BadRequestException("Version with ID " + request.getVersionId() + " is not a draft version.");
        }

        if(request.getReleaseDate() != null && request.getReleaseDate() < DateTimeHelper.currentTimeMillis()) {
            throw new BadRequestException("Release date cannot be in the past.");
        }

        gameVersion.setName(request.getVersionName());
        gameVersion.setChangeLog(request.getChangeLog());
        gameVersion.setExecPath(request.getExecPath());
        gameVersion.setDownloadUrl(request.getDownloadUrl());
        gameVersion.setReleaseDate(request.getReleaseDate());
        gameVersion.setFileSize(request.getFileSize());
        gameVersion.setInstallSize(request.getInstallSize());
        gameVersion.setChecksum(request.getChecksum());
        gameVersion.setUpdatedAt(DateTimeHelper.currentTimeMillis());
        gameVersion.setStatus(request.getIsReady() ? VersionStatus.STABLE : VersionStatus.DRAFT);

        try {
            gameVersionRepository.saveAndFlush(gameVersion);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update draft version for game ID " + request.getGameId(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDraftVersion(Long gameId, Long versionId) {
        if (!isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }
        GameVersion gameVersion = gameVersionRepository.findById(versionId)
                .orElseThrow(() -> new BadRequestException("Version with ID " + versionId + " does not exist."));
        if (!Objects.equals(gameVersion.getGame().getId(), gameId)) {
            throw new BadRequestException("Version with ID " + versionId + " does not belong to game ID " + gameId);
        }
        if (gameVersion.getStatus() != VersionStatus.DRAFT) {
            throw new BadRequestException("Version with ID " + versionId + " is not a draft version.");
        }
        try {
            gameVersionRepository.deleteById(versionId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete draft version with ID " + versionId + " for game ID " + gameId, e);
        }
    }

    @Override
    @Transactional
    public void markAsLatestStableNow(Long gameId, Long versionId) {
        if (!isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }

        GameVersion gameVersion = gameVersionRepository.findById(versionId)
                .orElseThrow(() -> new BadRequestException("Version with ID " + versionId + " does not exist."));

        long now = DateTimeHelper.currentTimeMillis();

        if (now < gameVersion.getReleaseDate()) {
            try {
                gameVersionRepository.updateStatusByGameAndStatus(VersionStatus.ARCHIVED,
                        Game.builder().id(gameId).build(), VersionStatus.STABLE, now);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update previous stable version for game ID " + gameId, e);
            }
        }

        gameVersion.setStatus(VersionStatus.STABLE);
        gameVersion.setReleaseDate(gameVersion.getReleaseDate() > now ? now : gameVersion.getReleaseDate());

        try {
            gameVersionRepository.save(gameVersion);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark version with ID " + versionId + " as stable for game ID " + gameId, e);
        }
    }

    @Override
    public List<GameVersionListItem> getGameVersions(Long gameId) {
        if (!isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }
        return gameVersionRepository.findGameVersionItemsByGameId(gameId);
    }

    private long getPublisherIdFromClaims() {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        return (long) claims.getOtherClaims().get("publisherId");
    }

    private boolean isGameOwnedByPublisher(Long gameId) {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        return gameRepository.existsByIdAndPublisherId(gameId, publisherId);
    }

}

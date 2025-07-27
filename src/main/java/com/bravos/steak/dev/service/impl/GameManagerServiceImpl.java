package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.dev.model.GameThumbnail;
import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.model.response.PublisherGameListItem;
import com.bravos.steak.dev.service.GameManagerService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.GameStoreDetail;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.GenreRepository;
import com.bravos.steak.store.repo.TagRepository;
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

    public GameManagerServiceImpl(ObjectMapper objectMapper, MongoTemplate mongoTemplate, GameRepository gameRepository,
                                  GenreRepository genreRepository, TagRepository tagRepository, SessionService sessionService,
                                  GameService gameService, GameDetailsRepository gameDetailsRepository) {
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.tagRepository = tagRepository;
        this.sessionService = sessionService;
        this.gameService = gameService;
        this.gameDetailsRepository = gameDetailsRepository;
    }

    @Override
    @Transactional
    public GameStoreDetail updateGameDetails(UpdateGameDetailsRequest request) {
        Map<String, Object> changedData = objectMapper.convertValue(request, new TypeReference<>() {
        });
        Long gameId = (Long) changedData.remove("gameId");

        if (isGameOwnedByPublisher(gameId)) {
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
        GameStatus gameStatus;
        try {
            gameStatus = GameStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid game status: " + status);
        }
        if (isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }
        try {
            gameRepository.updateStatusAndUpdatedAtById(gameStatus, DateTimeHelper.currentTimeMillis(), gameId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game status for game ID " + gameId, e);
        }
    }

    @Override
    @Transactional
    public void updateGamePrice(Long gameId, Double price) {
        if (isGameOwnedByPublisher(gameId)) {
            throw new BadRequestException("Game with ID " + gameId + " does not exist or is not owned by the publisher.");
        }
        if (price == null || price < 0 || price.isNaN() || price.isInfinite()) {
            throw new BadRequestException("Price must be a positive number.");
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

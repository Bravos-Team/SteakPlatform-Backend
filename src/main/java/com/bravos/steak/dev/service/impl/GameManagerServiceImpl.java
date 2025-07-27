package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.RedisCacheEntry;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.service.GameManagerService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.GenreRepository;
import com.bravos.steak.store.repo.TagRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GameManagerServiceImpl implements GameManagerService {

    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate;
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final SessionService sessionService;
    private final RedisService redisService;

    public GameManagerServiceImpl(ObjectMapper objectMapper, MongoTemplate mongoTemplate, GameRepository gameRepository,
                                  GenreRepository genreRepository, TagRepository tagRepository, SessionService sessionService, RedisService redisService) {
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.tagRepository = tagRepository;
        this.sessionService = sessionService;
        this.redisService = redisService;
    }

    @Override
    @Transactional
    public void updateGameDetails(UpdateGameDetailsRequest request) {
        Map<String,Object> changedData = objectMapper.convertValue(request, new TypeReference<>() {});
        Long gameId = (Long) changedData.remove("gameId");

        if(isGameOwnedByPublisher(gameId)) {
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
        if(tagIds != null && !tagIds.isEmpty()) {
            if (game == null) {
                game = gameRepository.findById(gameId)
                        .orElseThrow(() -> new BadRequestException("Game with ID " + gameId + " does not exist."));
            }
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
            game.setTags(tags);
        }
        if(request.getTitle() != null && !request.getTitle().isEmpty()) {
            if(game == null) {

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
        if(price == null || price < 0 || price.isNaN() || price.isInfinite()) {
            throw new BadRequestException("Price must be a positive number.");
        }
        try {
            gameRepository.updatePriceAndUpdatedAtById(BigDecimal.valueOf(price), DateTimeHelper.currentTimeMillis(), gameId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game price for game ID " + gameId, e);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        RedisCacheEntry<Collection<Genre>> cacheEntry = RedisCacheEntry.<Collection<Genre>>builder()
                .key("allGenres")
                .fallBackFunction(this::getAllGenresFromDatabase)
                .keyTimeout(15)
                .keyTimeUnit(TimeUnit.MINUTES)
                .build();
        CollectionLikeType type = objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, Genre.class);
        redisService.getWithLock(cacheEntry,type, Genre.class);

        return List.of();
    }

    @Override
    public List<Tag> getAllTags() {
        return List.of();
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

    private boolean isGameOwnedByPublisher(Long gameId) {
        JwtTokenClaims claims = (JwtTokenClaims) sessionService.getAuthentication().getDetails();
        long publisherId = (long) claims.getOtherClaims().get("publisherId");
        return gameRepository.existsByIdAndPublisherId(gameId, publisherId);
    }

}

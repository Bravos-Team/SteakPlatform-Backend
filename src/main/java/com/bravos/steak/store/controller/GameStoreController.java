package com.bravos.steak.store.controller;

import com.bravos.steak.store.model.request.FilterQuery;
import com.bravos.steak.store.service.GameService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/v1/store/public/games")
public class GameStoreController {

    GameService gameService;

    @GetMapping("/list")
    public ResponseEntity<?> getGameListStore(
            Optional<Long> cursor,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(gameService.getGameStoreList(cursor.orElse(null), pageSize));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getGameListStoreWithFilters(@RequestBody @Valid FilterQuery filterQuery) {
        return ResponseEntity.ok(gameService.getFilteredGames(filterQuery));
    }

    @GetMapping("/details")
    public ResponseEntity<?> getGameDetailsStore(
            @RequestParam Long gameId
    ) {
        return ResponseEntity.ok(gameService.getGameStoreDetails(gameId));
    }

    @GetMapping("/download/{gameId}")
    public ResponseEntity<?> getGameDownloadUrl(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameDownloadUrl(gameId));
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getGameGenres() {
        return ResponseEntity.ok(gameService.getAllGenres());
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getGameTags() {
        return ResponseEntity.ok(gameService.getAllTags());
    }

    @GetMapping("/newest")
    public ResponseEntity<?> getNewestGames(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(gameService.getNewestGames(page, pageSize));
    }

    @GetMapping("/coming-soon")
    public ResponseEntity<?> getComingSoonGames(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(gameService.getComingSoonGames(page, pageSize));
    }

}

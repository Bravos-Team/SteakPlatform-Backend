package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.*;
import com.bravos.steak.dev.service.GameManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@PublisherController
@RequestMapping("/api/v1/dev/game-management")
public class PublisherGameManagerController {

    private final GameManagerService gameManagerService;

    public PublisherGameManagerController(GameManagerService gameManagerService) {
        this.gameManagerService = gameManagerService;
    }

    @GetMapping("/list")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> listGames(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(gameManagerService.listAllGames(page - 1, size, status, keyword));
    }

    @PostMapping("/update-game-details")
    @HasAuthority({PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> updateGameDetails(@RequestBody @Valid UpdateGameDetailsRequest request) {
        return ResponseEntity.ok(gameManagerService.updateGameDetails(request));
    }

    @PostMapping("/update-game-status")
    @HasAuthority({PublisherAuthority.MANAGE_GAMES})
    public ResponseEntity<?> updateGameStatus(@RequestBody @Valid UpdateGameStatusRequest request) {
        gameManagerService.updateGameStatus(request.getGameId(), request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-game-price")
    @HasAuthority({PublisherAuthority.WRITE_GAME_PRICE})
    public ResponseEntity<?> updateGamePrice(@RequestBody @Valid UpdateGamePriceRequest request) {
        gameManagerService.updateGamePrice(request.getGameId(), request.getPrice());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-new-version")
    @HasAuthority({PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> createNewVersion(@RequestBody @Valid CreateNewVersionRequest request) {
        gameManagerService.createNewVersion(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-draft-version")
    @HasAuthority({PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> updateDraftVersion(@RequestBody @Valid UpdateVersionRequest request) {
        gameManagerService.updateDraftVersion(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-draft-version")
    @HasAuthority({PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> deleteDraftVersion(@RequestParam Long gameId, @RequestParam Long versionId) {
        gameManagerService.deleteDraftVersion(gameId, versionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-as-latest-stable")
    @HasAuthority({PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> markAsLatestStable(@RequestParam Long gameId, @RequestParam Long versionId) {
        gameManagerService.markAsLatestStableNow(gameId, versionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all-versions")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> getAllVersions(@RequestParam Long gameId) {
        return ResponseEntity.ok(gameManagerService.getGameVersions(gameId));
    }

    @GetMapping("/details/{gameId}")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> getGameDetails(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameManagerService.getFullGameDetails(gameId));
    }

    @GetMapping("/count")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> countGamesByStatus(@RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(gameManagerService.countGamesByStatus(status));
    }

    @GetMapping("/current-version/{gameId}")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> getGameCurrentVersion(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameManagerService.getGameCurrentVersion(gameId));
    }

}

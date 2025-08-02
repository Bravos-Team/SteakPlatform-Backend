package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.response.GameListItem;
import com.bravos.steak.administration.service.AdminGameService;
import com.bravos.steak.common.annotation.AdminController;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/games")
public class AdminGameController {

    private final AdminGameService adminGameService;

    public AdminGameController(AdminGameService adminGameService) {
        this.adminGameService = adminGameService;
    }

    @GetMapping
    public ResponseEntity<Page<GameListItem>> getAllGames(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GameListItem> games = adminGameService.getAllGames(page - 1, size);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GameListItem>> searchGames(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GameListItem> games = adminGameService.searchGames(query, page - 1, size);
        return ResponseEntity.ok(games);
    }

    @PutMapping("/{gameId}/status")
    public ResponseEntity<Void> updateGameStatus(
            @PathVariable Long gameId,
            @RequestParam String status) {
        adminGameService.updateGameStatus(gameId, status);
        return ResponseEntity.noContent().build();
    }
}

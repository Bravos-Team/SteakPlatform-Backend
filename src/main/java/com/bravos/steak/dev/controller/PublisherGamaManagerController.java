package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.UpdateGameDetailsRequest;
import com.bravos.steak.dev.model.request.UpdateGamePriceRequest;
import com.bravos.steak.dev.model.request.UpdateGameStatusRequest;
import com.bravos.steak.dev.service.GameManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@PublisherController
@RequestMapping("/api/v1/dev/game-management")
public class PublisherGamaManagerController {

    private final GameManagerService gameManagerService;

    public PublisherGamaManagerController(GameManagerService gameManagerService) {
        this.gameManagerService = gameManagerService;
    }

    @GetMapping("/list")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> listGames(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String status) {
        return ResponseEntity.ok(gameManagerService.listAllGames(page - 1, size, status));
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

}

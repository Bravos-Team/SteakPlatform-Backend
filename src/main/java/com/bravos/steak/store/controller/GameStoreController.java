package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.GameService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(gameService.getGameStoreList(cursor.orElse(null),pageSize));
    }

    @GetMapping("/details")
    public ResponseEntity<?> getGameDetailsStore(
            @RequestParam Long gameId
    ) {
        return ResponseEntity.ok(gameService.getGameStoreDetails(gameId));
    }

}

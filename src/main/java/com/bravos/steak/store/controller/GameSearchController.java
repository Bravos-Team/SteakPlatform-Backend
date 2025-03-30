package com.bravos.steak.store.controller;

import com.bravos.steak.store.model.response.GameResponse;
import com.bravos.steak.store.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game/search")
public class GameSearchController {

    private final GameService gameService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getGameById(@PathVariable Long id) {
        try {
            log.info("Get Game by id: {}", id);
            GameResponse game = gameService.findById(id);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Page<?>> getGameByTerm(@PathVariable String name, @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching for game: {}, Page: {}", name, pageable);
        Page<GameResponse> response = gameService.findByName(name, pageable);
        log.info("Found {} games", response.getTotalElements());
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

}

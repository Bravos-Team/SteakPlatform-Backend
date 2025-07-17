package com.bravos.steak.store.controller;

import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.service.impl.GameServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/v1/store/public")
public class GameStoreController {

    GameServiceImpl gameService;

    @GetMapping("games")
    public ResponseEntity<?> getGameListStore(
            @RequestParam(defaultValue = "0", required = false) Long cursor,
            @RequestParam(defaultValue = "0", required = false) BigDecimal minPrice,
            @RequestParam(defaultValue = "100000", required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "OPENING", required = false) GameStatus status,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "", required = false) List<String> platforms
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                gameService.getFilteredGames(cursor, minPrice, maxPrice, status, platforms, pageSize)
        );
    }
}

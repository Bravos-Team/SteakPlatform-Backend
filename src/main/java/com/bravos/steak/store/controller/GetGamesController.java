package com.bravos.steak.store.controller;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.mapper.GameMapper;
import com.bravos.steak.store.model.response.GameResponse;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.service.impl.GameServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game/get-games")
public class GetGamesController {

    private final GameServiceImpl gameService;

    @GetMapping
    public ResponseEntity<Page<GameResponse>> getAllGames(@PageableDefault(size = 20) Pageable pageable, @RequestParam(required = false) List<String> search) {
        Page<GameResponse> response;

        if (search == null || search.isEmpty())
            response = gameService.findAll(pageable);
        else
            response = gameService.findGameBySlugs(search, pageable);

        return ResponseEntity.ok(response);
    }

}

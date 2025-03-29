package com.bravos.steak.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @GetMapping("/")
    public ResponseEntity<String> getAllGames() {
        System.out.println("getAllGames.");
        return ResponseEntity.ok("All games");
    }

}

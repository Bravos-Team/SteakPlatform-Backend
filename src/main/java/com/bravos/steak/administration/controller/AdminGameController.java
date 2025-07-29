package com.bravos.steak.administration.controller;

import com.bravos.steak.common.annotation.AdminController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/games")
public class AdminGameController {

    @GetMapping("/list")
    public ResponseEntity<?> getGameList(@RequestParam Optional<String> keyword,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "20") Integer size) {
        // Placeholder for actual implementation
        return ResponseEntity.ok("Game list retrieved successfully");
    }

    @GetMapping("/details")
    public ResponseEntity<?> getGameDetails(@RequestParam Long gameId) {
        // Placeholder for actual implementation
        return ResponseEntity.ok("Game details retrieved successfully for game ID: " + gameId);
    }

    @PostMapping("/change-status")
    public ResponseEntity<?> changeGameStatus(@RequestParam Long gameId,
                                              @RequestParam String status) {
        // Placeholder for actual implementation
        return ResponseEntity.ok("Game status changed successfully for game ID: " + gameId +
                " to status: " + status);
    }

}

package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.AdminAuthority;
import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.repo.GameSubmissionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/review-game")
public class AdminReviewGameController {

    private final GameSubmissionRepository gameSubmissionRepository;

    public AdminReviewGameController(GameSubmissionRepository gameSubmissionRepository) {
        this.gameSubmissionRepository = gameSubmissionRepository;
    }

    @HasAuthority({AdminAuthority.REVIEW_GAME})
    @GetMapping("/list")
    public ResponseEntity<?> getNeedReviewGames(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "20") Integer size,
                                                @RequestParam Optional<String> keyword) {

        return ResponseEntity.ok(gameSubmissionRepository.getGameSubmissionListDisplay(
                null,
                GameSubmissionStatus.PENDING_REVIEW,
                keyword.orElse(null),
                page - 1,
                size
        ));
    }

}

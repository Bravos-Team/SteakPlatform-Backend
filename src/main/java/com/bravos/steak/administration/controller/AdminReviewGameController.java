package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.AdminAuthority;
import com.bravos.steak.administration.service.GameReviewService;
import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import org.springframework.data.domain.Sort;
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

    private final GameReviewService gameReviewService;

    public AdminReviewGameController(GameReviewService gameReviewService) {
        this.gameReviewService = gameReviewService;
    }

    @HasAuthority({AdminAuthority.REVIEW_GAME})
    @GetMapping("/list/need-review")
    public ResponseEntity<?> getNeedReviewGames(@RequestParam Optional<String> keyword,
                                                @RequestParam Optional<Sort> sort,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "20") Integer size,
                                                @RequestParam Optional<Long> publisherId) {
        return ResponseEntity.ok(gameReviewService.getNeedReviewGames(
                GameSubmissionStatus.PENDING_REVIEW,
                keyword.orElse(null),
                publisherId.orElse(null),
                page - 1,
                size,
                sort.orElse(Sort.by(Sort.Direction.DESC, "updatedAt"))
        ));
    }

    @HasAuthority({AdminAuthority.REVIEW_GAME})
    @GetMapping("/list/rejected")
    public ResponseEntity<?> getRejectedGames(@RequestParam Optional<String> keyword,
                                              @RequestParam Optional<Sort> sort,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size,
                                              @RequestParam Optional<Long> publisherId) {
        return ResponseEntity.ok(gameReviewService.getNeedReviewGames(
                GameSubmissionStatus.REJECTED,
                keyword.orElse(null),
                publisherId.orElse(null),
                page - 1,
                size,
                sort.orElse(Sort.by(Sort.Direction.DESC, "updatedAt"))
        ));
    }

    @HasAuthority({AdminAuthority.REVIEW_GAME})
    @GetMapping("/list/accepted")
    public ResponseEntity<?> getAcceptedGames(@RequestParam Optional<String> keyword,
                                              @RequestParam Optional<Sort> sort,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size,
                                              @RequestParam Optional<Long> publisherId) {
        return ResponseEntity.ok(gameReviewService.getNeedReviewGames(
                GameSubmissionStatus.ACCEPTED,
                keyword.orElse(null),
                publisherId.orElse(null),
                page - 1,
                size,
                sort.orElse(Sort.by(Sort.Direction.DESC, "updatedAt"))
        ));
    }

}

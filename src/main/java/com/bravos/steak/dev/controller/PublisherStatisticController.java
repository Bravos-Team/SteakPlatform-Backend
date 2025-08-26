package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.service.GameStatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PublisherController
@RequestMapping("/api/v1/dev/statistics")
public class PublisherStatisticController {

    private final GameStatisticService gameStatisticService;

    public PublisherStatisticController(GameStatisticService gameStatisticService) {
        this.gameStatisticService = gameStatisticService;
    }

    @GetMapping("/games/revenue")
    public ResponseEntity<?> getGameStatisticsRevenue(@RequestParam(required = false) Integer month,
                                                      @RequestParam(required = false) Integer year,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(gameStatisticService.getGameStatisticsRevenue(month, year, page - 1, pageSize));
    }

}

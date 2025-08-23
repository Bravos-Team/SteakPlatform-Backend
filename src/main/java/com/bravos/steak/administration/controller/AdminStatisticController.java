package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.AdminAuthority;
import com.bravos.steak.administration.service.AdminStatisticService;
import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.common.annotation.HasAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@AdminController
@HasAuthority({AdminAuthority.READ_STATISTICS})
@RequestMapping("/api/v1/admin/statistics")
public class AdminStatisticController {

    private final AdminStatisticService adminStatisticService;

    public AdminStatisticController(AdminStatisticService adminStatisticService) {
        this.adminStatisticService = adminStatisticService;
    }

    @GetMapping("/yearly-revenue")
    public ResponseEntity<?> getRevenueStatisticByYear() {
        return ResponseEntity.ok(adminStatisticService.getRevenueStatisticByYear());
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<?> getRevenueStatisticByMonth(@RequestParam(value = "year") Integer year) {
        if (year == null || year < 2000 || year > 2100) {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(adminStatisticService.getRevenueStatisticByMonth(year));
    }

    @GetMapping("/daily-revenue")
    public ResponseEntity<?> getRevenueStatisticByDay(
            @RequestParam(value = "month") Integer month,
            @RequestParam(value = "year") Integer year) {
        if (month == null || month < 1 || month > 12) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == null || year < 2000 || year > 2100) {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(adminStatisticService.getRevenueStatisticByDay(month, year));
    }

}

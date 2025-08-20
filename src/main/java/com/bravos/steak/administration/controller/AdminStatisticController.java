package com.bravos.steak.administration.controller;

import com.bravos.steak.common.annotation.AdminController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/statistics")
public class AdminStatisticController {

    public ResponseEntity<?> getRevenueStatisticByYear() {
        return null;
    }

    public ResponseEntity<?> getRevenueStatisticByMonth() {
        return null;
    }

    public ResponseEntity<?> getRevenueStatisticByDay() {
        return null;
    }

}

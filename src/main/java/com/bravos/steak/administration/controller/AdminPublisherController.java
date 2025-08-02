package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.response.PublisherListItem;
import com.bravos.steak.administration.service.AdminPublisherService;
import com.bravos.steak.common.annotation.AdminController;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/publishers")
public class AdminPublisherController {

    private final AdminPublisherService adminPublisherService;

    public AdminPublisherController(AdminPublisherService adminPublisherService) {
        this.adminPublisherService = adminPublisherService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPublishers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PublisherListItem> publishers = adminPublisherService.getAllPublishers(page - 1, size);
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPublishers(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PublisherListItem> publishers = adminPublisherService.searchPublishers(query, page - 1, size);
        return ResponseEntity.ok(publishers);
    }

    @PostMapping("/{publisherId}/status")
    public ResponseEntity<Void> updatePublisherStatus(
            @PathVariable Long publisherId,
            @RequestParam String status) {
        adminPublisherService.updatePublisherStatus(publisherId, status);
        return ResponseEntity.ok().build();
    }

}

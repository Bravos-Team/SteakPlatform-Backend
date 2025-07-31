package com.bravos.steak.dev.controller;

import com.bravos.steak.dev.service.PublisherManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/public")
public class PublisherPublicController {

    private final PublisherManagerService publisherManagerService;

    public PublisherPublicController(PublisherManagerService publisherManagerService) {
        this.publisherManagerService = publisherManagerService;
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<?> getPublisherPublicInfo(@PathVariable Long publisherId) {
        return ResponseEntity.ok(publisherManagerService.getPublisherById(publisherId));
    }

    @GetMapping("/publisher/current")
    public ResponseEntity<?> getCurrentPublisherPublicInfo() {
        return ResponseEntity.ok(publisherManagerService.getCurrentPublisher());
    }

}

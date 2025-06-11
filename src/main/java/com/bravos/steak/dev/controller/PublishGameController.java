package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.service.PublisherPublishGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dev/publish")
@PublisherController
public class PublishGameController {

    private final PublisherPublishGameService publisherPublishGameService;

    public PublishGameController(PublisherPublishGameService publisherPublishGameService) {
        this.publisherPublishGameService = publisherPublishGameService;
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/create-project")
    public ResponseEntity<?> createProject(@RequestParam String name) {
        return ResponseEntity.ok(Map.of(
                "projectId",publisherPublishGameService.createProject(name)
        ));
    }

}

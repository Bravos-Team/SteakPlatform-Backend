package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import com.bravos.steak.dev.service.PublisherPublishGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
                "projectId", publisherPublishGameService.createProject(name)
        ));
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/update-project")
    public ResponseEntity<?> saveDraftProject(@RequestBody @Validated SaveProjectRequest saveProjectRequest) {
        publisherPublishGameService.saveDraftProject(saveProjectRequest);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/update-build")
    public ResponseEntity<?> updateBuild(@RequestBody @Validated UpdatePreBuildRequest updatePreBuildRequest) {
        publisherPublishGameService.updateBuild(updatePreBuildRequest);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/publish")
    public ResponseEntity<?> publishGame(@RequestParam Long projectId) {
        if(projectId == null || projectId <= 900000000000000L) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid project ID"
            ));
        }
        publisherPublishGameService.publishGame(projectId);
        return ResponseEntity.ok().body(Map.of(
                "message", "Game published successfully"
        ));
    }

}

package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import com.bravos.steak.dev.service.PublisherPublishGameService;
import com.bravos.steak.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dev/project")
@PublisherController
public class PublisherPublishGameController {

    private final PublisherPublishGameService publisherPublishGameService;

    public PublisherPublishGameController(PublisherPublishGameService publisherPublishGameService) {
        this.publisherPublishGameService = publisherPublishGameService;
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestParam String name) {
        return ResponseEntity.ok(Map.of(
                "projectId", publisherPublishGameService.createProject(name)
        ));
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/update")
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
        publisherPublishGameService.submitGameSubmission(projectId);
        return ResponseEntity.ok().body(Map.of(
                "message", "Game published successfully"
        ));
    }

    @GetMapping("/list")
    @HasAuthority({PublisherAuthority.READ_GAMES})
    public ResponseEntity<?> getProjectListByPublisher(@RequestParam Optional<String> status,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "20") Integer size,
                                                       @RequestParam Optional<String> keyword) {
        if(page < 1 || size <= 0) {
            throw new BadRequestException("Please check page and size values.");
        }
        return ResponseEntity.ok(publisherPublishGameService.getProjectListByPublisher
                (status.orElse(null),keyword.orElse(null),page - 1,size)
        );
    }



}

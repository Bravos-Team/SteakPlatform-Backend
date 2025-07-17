package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import com.bravos.steak.dev.service.GameSubmissionService;
import com.bravos.steak.exceptions.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dev/project")
@PublisherController
public class PublisherPublishGameController {

    private final GameSubmissionService gameSubmissionService;
    private final SessionService sessionService;

    public PublisherPublishGameController(GameSubmissionService gameSubmissionService, SessionService sessionService) {
        this.gameSubmissionService = gameSubmissionService;
        this.sessionService = sessionService;
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestParam String name) {
        return ResponseEntity.ok(Map.of(
                "projectId", gameSubmissionService.createProject(name)
        ));
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/update")
    public ResponseEntity<?> saveDraftProject(@RequestBody @Valid SaveProjectRequest saveProjectRequest) {
        gameSubmissionService.saveDraftProject(saveProjectRequest);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/update-build")
    public ResponseEntity<?> updateBuild(@RequestBody @Valid UpdatePreBuildRequest updatePreBuildRequest) {
        gameSubmissionService.updateBuildProject(updatePreBuildRequest);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({PublisherAuthority.CREATE_GAME})
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestParam Long projectId) {
        if(projectId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid project ID"
            ));
        }
        gameSubmissionService.submitGameSubmission(projectId);
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
        return ResponseEntity.ok(gameSubmissionService.getProjectListByPublisher
                (status.orElse(null),keyword.orElse(null),page - 1,size)
        );
    }

    @GetMapping("/detail/{projectId}")
    public ResponseEntity<?> detailProject(@PathVariable Long projectId) {
        if(projectId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid project ID"
            ));
        }
        JwtAuthentication authentication = sessionService.getAuthentication();
        JwtTokenClaims claims = (JwtTokenClaims) authentication.getDetails();
        Long publisherId = (Long) claims.getOtherClaims().get("publisherId");
        return ResponseEntity.ok(gameSubmissionService.detailByIdAndPublisher(projectId,publisherId));
    }



}

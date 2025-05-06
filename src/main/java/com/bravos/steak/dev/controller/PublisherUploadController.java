package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.service.PublisherUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/upload")
public class PublisherUploadController {

    private final PublisherUploadService publisherUploadService;

    public PublisherUploadController(PublisherUploadService publisherUploadService) {
        this.publisherUploadService = publisherUploadService;
    }

    @GetMapping("/image/publisher/presigned")
    @HasAuthority({PublisherAuthority.MASTER,PublisherAuthority.WRITE_INFO})
    public ResponseEntity<?> getUploadPresignedUrl(@RequestBody ImageUploadPresignedRequest imageUploadPresignedRequest) {
        return ResponseEntity.ok(publisherUploadService.createUploadPublisherImagePresignedUrl(imageUploadPresignedRequest));
    }

    @GetMapping("/image/publisher/multi-presigned")
    @HasAuthority({PublisherAuthority.CREATE_GAME,PublisherAuthority.WRITE_GAME_INFO})
    public ResponseEntity<?> getMultiPresignedUrl() {
        return null;
    }

}

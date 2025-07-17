package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.*;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import com.bravos.steak.dev.service.PublisherUploadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@PublisherController
@RequestMapping("/api/v1/dev/upload")
public class PublisherUploadController {

    private final PublisherUploadService publisherUploadService;

    public PublisherUploadController(PublisherUploadService publisherUploadService) {
        this.publisherUploadService = publisherUploadService;
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @PostMapping("/presigned-image-url")
    public ResponseEntity<?> getUploadPresignUrl(@RequestBody @Valid ImageUploadPresignedRequest request) {
        PresignedUrlResponse response = publisherUploadService.createPublisherPresignedImageUrl(request);
        return ResponseEntity.ok(response);
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @PostMapping("/presigned-image-urls")
    public ResponseEntity<?> getUploadPresignUrls(@RequestBody @Valid ImageUploadPresignedRequest[] request) {
        if (request == null || request.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        PresignedUrlResponse[] response = publisherUploadService.createPublisherPresignedImageUrls(request);
        return ResponseEntity.ok(response);
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @DeleteMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody @Valid DeleteImageRequest deleteImageRequest) {
        publisherUploadService.deletePublisherImage(deleteImageRequest);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @DeleteMapping("/delete-images")
    public ResponseEntity<?> deleteImages(@RequestBody @Valid DeleteImageRequest[] deleteImageRequests) {
        if (deleteImageRequests == null || deleteImageRequests.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        publisherUploadService.deletePublisherImages(deleteImageRequests);
        return ResponseEntity.ok().build();
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @PostMapping("/presigned-part-game-url")
    public ResponseEntity<?> getPartUploadPresignedUrl(@RequestBody @Valid GameUploadPresignedRequest request) {
        var response = publisherUploadService.createPublisherPartUploadPresignedUrl(request);
        return ResponseEntity.ok(response);
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @PostMapping("/recreate-presigned-upload-url")
    public ResponseEntity<?> recreatePresignedUploadUrl(@RequestBody @Valid RecreatePresignedUrlRequest request) {
        var response = publisherUploadService.recreatePresignedUploadUrl(request);
        return ResponseEntity.ok(response);
    }

    @HasAuthority({
            PublisherAuthority.CREATE_GAME,
            PublisherAuthority.MANAGE_GAMES,
            PublisherAuthority.WRITE_GAME_INFO,
            PublisherAuthority.WRITE_INFO
    })
    @PostMapping("/complete-part-upload")
    public ResponseEntity<?> completePartUpload(@RequestBody @Valid CompleteMultipartRequest request) {
        return ResponseEntity.ok(publisherUploadService.completeMultipartUpload(request));
    }


}

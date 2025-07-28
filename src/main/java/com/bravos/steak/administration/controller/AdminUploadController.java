package com.bravos.steak.administration.controller;

import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/upload")
public class AdminUploadController {

    @PostMapping("/presigned-image-url")
    public ResponseEntity<?> getUploadPresignUrl(@RequestBody @Valid ImageUploadPresignedRequest request) {
        // This method is intentionally left blank for future implementation.
        // It should return a presigned URL for image uploads.
        return ResponseEntity.ok("Presigned URL for image upload will be implemented here.");
    }

    @PostMapping("/presigned-image-urls")
    public ResponseEntity<?> getUploadPresignUrls(@RequestBody @Valid ImageUploadPresignedRequest[] request) {
        // This method is intentionally left blank for future implementation.
        // It should return presigned URLs for multiple image uploads.
        if (request == null || request.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        return ResponseEntity.ok("Presigned URLs for image uploads will be implemented here.");
    }

    @DeleteMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody @Valid DeleteImageRequest deleteImageRequest) {
        // This method is intentionally left blank for future implementation.
        // It should handle the deletion of a single image based on the provided request.
        if (deleteImageRequest == null) {
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }
        return ResponseEntity.ok("Deletion of single image will be implemented here.");

    }

    @DeleteMapping("/delete-images")
    public ResponseEntity<?> deleteImages(@RequestBody @Valid DeleteImageRequest[] deleteImageRequests) {
        // This method is intentionally left blank for future implementation.
        // It should handle the deletion of multiple images based on the provided requests.
        if (deleteImageRequests == null || deleteImageRequests.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        return ResponseEntity.ok("Deletion of multiple images will be implemented here.");
    }

}

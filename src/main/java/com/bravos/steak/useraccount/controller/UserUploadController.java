package com.bravos.steak.useraccount.controller;

import com.bravos.steak.administration.service.AdminUploadService;
import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/upload")
public class UserUploadController {

    private final AdminUploadService adminUploadService;

    public UserUploadController(AdminUploadService adminUploadService) {
        this.adminUploadService = adminUploadService;
    }

    @PostMapping("/presigned-upload-url")
    public ResponseEntity<?> getUploadPresignUrl(@RequestBody @Valid ImageUploadPresignedRequest request) {
        return ResponseEntity.ok(adminUploadService.createAdminPresignedUploadUrl(request));
    }

    @PostMapping("/presigned-upload-urls")
    public ResponseEntity<?> getUploadPresignUrls(@RequestBody @Valid ImageUploadPresignedRequest[] request) {
        if (request == null || request.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        return ResponseEntity.ok(adminUploadService.createAdminPresignedUploadUrls(request));
    }

    @DeleteMapping("/delete-file")
    public ResponseEntity<?> deleteImage(@RequestBody @Valid DeleteImageRequest deleteImageRequest) {
        adminUploadService.deleteAdminFile(deleteImageRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-files")
    public ResponseEntity<?> deleteImages(@RequestBody @Valid DeleteImageRequest[] deleteImageRequests) {
        adminUploadService.deleteAdminFile(deleteImageRequests);
        return ResponseEntity.ok().build();
    }

}

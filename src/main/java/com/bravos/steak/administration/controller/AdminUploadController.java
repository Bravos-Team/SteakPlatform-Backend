package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.AdminAuthority;
import com.bravos.steak.administration.service.AdminUploadService;
import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/upload")
public class AdminUploadController {

    private final AdminUploadService adminUploadService;

    public AdminUploadController(AdminUploadService adminUploadService) {
        this.adminUploadService = adminUploadService;
    }

    @PostMapping("/presigned-upload-url")
    @HasAuthority({
            AdminAuthority.REVIEW_GAME,
            AdminAuthority.MANAGE_GAMES,
            AdminAuthority.MANAGE_ADMIN_ACCOUNTS
    })
    public ResponseEntity<?> getUploadPresignUrl(@RequestBody @Valid ImageUploadPresignedRequest request) {
        return ResponseEntity.ok(adminUploadService.createAdminPresignedUploadUrl(request));
    }

    @PostMapping("/presigned-upload-urls")
    @HasAuthority({
            AdminAuthority.REVIEW_GAME,
            AdminAuthority.MANAGE_GAMES,
            AdminAuthority.MANAGE_ADMIN_ACCOUNTS
    })
    public ResponseEntity<?> getUploadPresignUrls(@RequestBody @Valid ImageUploadPresignedRequest[] request) {
        if (request == null || request.length == 0) {
            return ResponseEntity.badRequest().body("Request body cannot be null or empty");
        }
        return ResponseEntity.ok(adminUploadService.createAdminPresignedUploadUrls(request));
    }

    @DeleteMapping("/delete-file")
    @HasAuthority({
            AdminAuthority.REVIEW_GAME,
            AdminAuthority.MANAGE_GAMES,
            AdminAuthority.MANAGE_ADMIN_ACCOUNTS
    })
    public ResponseEntity<?> deleteImage(@RequestBody @Valid DeleteImageRequest deleteImageRequest) {
        adminUploadService.deleteAdminFile(deleteImageRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-files")
    @HasAuthority({
            AdminAuthority.REVIEW_GAME,
            AdminAuthority.MANAGE_GAMES,
            AdminAuthority.MANAGE_ADMIN_ACCOUNTS
    })
    public ResponseEntity<?> deleteImages(@RequestBody @Valid DeleteImageRequest[] deleteImageRequests) {
        adminUploadService.deleteAdminFile(deleteImageRequests);
        return ResponseEntity.ok().build();
    }

}

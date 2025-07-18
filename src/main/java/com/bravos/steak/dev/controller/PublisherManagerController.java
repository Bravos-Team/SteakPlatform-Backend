package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.service.PublisherManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/manager")
@PublisherController
public class PublisherManagerController {

    private final PublisherManagerService publisherManagerService;

    public PublisherManagerController(PublisherManagerService publisherManagerService) {
        this.publisherManagerService = publisherManagerService;
    }

    @GetMapping("/accounts")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> getPublisherAccounts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(publisherManagerService.getPublisherAccounts(page, size, status));
    }

    @GetMapping("/custom-roles")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> getCustomRoleList() {
        return ResponseEntity.ok(publisherManagerService.getCustomRoleList());
    }

}

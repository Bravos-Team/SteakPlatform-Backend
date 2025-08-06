package com.bravos.steak.dev.controller;

import com.bravos.steak.common.annotation.HasAuthority;
import com.bravos.steak.common.annotation.PublisherController;
import com.bravos.steak.dev.model.PublisherAuthority;
import com.bravos.steak.dev.model.request.CreateCustomRoleRequest;
import com.bravos.steak.dev.model.request.CreatePublisherAccountRequest;
import com.bravos.steak.dev.service.PublisherManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/accounts/search")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> searchPublisherAccounts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(publisherManagerService.searchPublisherAccounts(keyword, status, page, size));
    }

    @GetMapping("/permissions")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> getPublisherPermissions() {
        return ResponseEntity.ok(publisherManagerService.getPublisherPermissions());
    }

    @GetMapping("/custom-roles")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> getCustomRoleList() {
        return ResponseEntity.ok(publisherManagerService.getCustomRoleList());
    }

    @GetMapping("/me")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> myAccountDetail() {
        return ResponseEntity.ok(publisherManagerService.myAccountDetail());
    }

    @GetMapping("/account")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> getAccountDetail(@RequestParam Long accountId) {
        return ResponseEntity.ok(publisherManagerService.getAccountDetail(accountId));
    }

    @GetMapping("/role")
    @HasAuthority({PublisherAuthority.READ_MEMBERS})
    public ResponseEntity<?> getRoleDetail(@RequestParam Long roleId) {
        return ResponseEntity.ok(publisherManagerService.getRoleDetail(roleId));
    }

    @PostMapping("/create-account")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> createAccount(@RequestBody @Valid CreatePublisherAccountRequest request) {
        return ResponseEntity.ok(publisherManagerService.createAccount(request));
    }

    @DeleteMapping("/delete-account")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> deleteAccount(@RequestParam Long accountId) {
        publisherManagerService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/create-role")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> createNewCustomRole(@RequestBody @Valid CreateCustomRoleRequest request) {
        return ResponseEntity.ok(publisherManagerService.createNewCustomRole(request));
    }

    @PostMapping("/role/update-role")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> updateCustomRole(@RequestBody @Valid CreateCustomRoleRequest request) {
        return ResponseEntity.ok(publisherManagerService.updateRole(request));
    }

    @PostMapping("/role/change-status")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> changeRoleStatus(@RequestParam Long roleId, @RequestParam Boolean isActive) {
        publisherManagerService.changeRoleStatus(roleId, isActive);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/detach-role")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> removeAccountFromRole(@RequestParam Long roleId, @RequestParam Long accountId) {
        publisherManagerService.removeAccountFromRole(roleId, accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/assign-role")
    @HasAuthority({PublisherAuthority.WRITE_MEMBERS})
    public ResponseEntity<?> assignAccountToRole(@RequestParam Long roleId, @RequestParam Long accountId) {
        publisherManagerService.assignAccountToRole(roleId, accountId);
        return ResponseEntity.ok().build();
    }

}

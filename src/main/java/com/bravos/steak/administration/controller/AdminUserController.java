package com.bravos.steak.administration.controller;

import com.bravos.steak.administration.model.AdminAuthority;
import com.bravos.steak.administration.service.AdminUserService;
import com.bravos.steak.common.annotation.AdminController;
import com.bravos.steak.common.annotation.HasAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AdminController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    @HasAuthority({AdminAuthority.READ_USERS})
    public ResponseEntity<?> getAllUsers(@RequestParam int page,
                                         @RequestParam int size) {
        return ResponseEntity.ok(adminUserService.getAllUsers(page - 1, size));
    }

    @GetMapping("/search")
    @HasAuthority({AdminAuthority.READ_USERS})
    public ResponseEntity<?> searchUsers(@RequestParam String query,
                                         @RequestParam int page,
                                         @RequestParam int size) {
        return ResponseEntity.ok(adminUserService.searchUsers(query, page - 1, size));
    }

    @GetMapping("/count")
    @HasAuthority({AdminAuthority.READ_USERS})
    public ResponseEntity<Long> countUsers() {
        long count = adminUserService.countUsers();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{userId}/status")
    @HasAuthority({AdminAuthority.MANAGE_USERS})
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId,
                                              @RequestParam String status) {
        adminUserService.updateUserStatus(userId, status);
        return ResponseEntity.ok().build();
    }

}

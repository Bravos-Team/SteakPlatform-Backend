package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.model.response.UserListItem;
import com.bravos.steak.administration.service.AdminUserService;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private final UserAccountRepository userAccountRepository;
    private final SessionService sessionService;

    public AdminUserServiceImpl(UserAccountRepository userAccountRepository, SessionService sessionService) {
        this.userAccountRepository = userAccountRepository;
        this.sessionService = sessionService;
    }

    @Override
    public Page<UserListItem> getAllUsers(int page, int size) {
        return userAccountRepository.getAllUsers(PageRequest.of(page, size));
    }

    @Override
    public Page<UserListItem> searchUsers(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            return getAllUsers(page, size);
        }
        if (query.matches(EMAIL_REGEX)) {
            return userAccountRepository.getUsersByEmail(query, PageRequest.of(page, size));
        }
        return userAccountRepository.getUsersByUsername(query, PageRequest.of(page, size));
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        AccountStatus accountStatus;
        try {
            accountStatus = AccountStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account status: " + status);
        }
        try {
            int changed = userAccountRepository.updateStatusById(accountStatus, userId);
            if (changed != 0) {
                sessionService.invalidateUserToken(userId);
            }
        } catch (Exception e) {
            log.error("Failed to update user status for userId: {}, status: {}", userId, status, e);
            throw new RuntimeException("Failed to update user status", e);
        }
    }

    @Override
    public long countUsers() {
        return userAccountRepository.count();
    }

}

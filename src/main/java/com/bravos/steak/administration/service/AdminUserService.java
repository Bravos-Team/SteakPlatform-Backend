package com.bravos.steak.administration.service;

import com.bravos.steak.administration.model.response.UserListItem;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminUserService {

    Page<UserListItem> getAllUsers(int page, int size);

    Page<UserListItem> searchUsers(String query, int page, int size);

    void updateUserStatus(Long userId, String status);

    long countUsers();
}

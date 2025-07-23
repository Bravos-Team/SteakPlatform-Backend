package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.CreateCustomRoleRequest;
import com.bravos.steak.dev.model.request.CreatePublisherAccountRequest;
import com.bravos.steak.dev.model.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PublisherManagerService {

    Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status);

    Page<PublisherAccountListItem> searchPublisherAccounts(String keyword, String status,
                                                           int page, int size);

    List<GroupPermissionItem> getPublisherPermissions();

    List<RoleListItem> getCustomRoleList();

    RoleDetail getRoleDetail(Long roleId);

    PublisherAccountDetail myAccountDetail();

    PublisherAccountDetail getAccountDetail(Long accountId);

    PublisherAccountDetail createAccount(CreatePublisherAccountRequest request);

    RoleDetail createNewCustomRole(CreateCustomRoleRequest request);

    RoleDetail updateRole(CreateCustomRoleRequest request);

    void changeRoleStatus(Long roleId, Boolean isActive);

    void removeAccountFromRole(Long roleId, Long accountId);

    void assignAccountToRole(Long roleId, Long accountId);

    void deleteAccount(Long accountId);
}

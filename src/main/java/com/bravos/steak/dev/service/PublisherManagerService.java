package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.CreateCustomRoleRequest;
import com.bravos.steak.dev.model.request.CreatePublisherAccountRequest;
import com.bravos.steak.dev.model.response.PublisherAccountDetail;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.model.response.RoleDetail;
import com.bravos.steak.dev.model.response.RoleListItem;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PublisherManagerService {

    Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status);

    List<RoleListItem> getCustomRoleList();

    RoleDetail getRoleDetail(Long roleId);

    PublisherAccountDetail myAccountDetail();

    PublisherAccountDetail getAccountDetail(Long accountId);

    PublisherAccountDetail createAccount(CreatePublisherAccountRequest request);

    RoleDetail createNewCustomRole(CreateCustomRoleRequest request);

    void changeRoleStatus(Long roleId, Boolean isActive);

    void removeAccountFromRole(Long roleId, Long accountId);

    void assignAccountToRole(Long roleId, Long accountId);

}

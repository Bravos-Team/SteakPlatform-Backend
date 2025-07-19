package com.bravos.steak.dev.service;

import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PublisherManagerService {

    Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status);

    List<PublisherRole> getCustomRoleList();

}

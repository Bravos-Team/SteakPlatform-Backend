package com.bravos.steak.dev.service.impl;

import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.service.PublisherManagerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherManagerServiceImpl implements PublisherManagerService {

    private final PublisherAccountRepository publisherAccountRepository;

    public PublisherManagerServiceImpl(PublisherAccountRepository publisherAccountRepository) {
        this.publisherAccountRepository = publisherAccountRepository;
    }

    @Override
    public Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status) {
        return null;
    }

    @Override
    public List<PublisherRole> getCustomRoleList() {
        return List.of();
    }

}

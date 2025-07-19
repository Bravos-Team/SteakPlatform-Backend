package com.bravos.steak.dev.service.impl;

import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.service.PublisherManagerService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        if("all".equalsIgnoreCase(status)) {
            return publisherAccountRepository.findAllz(PageRequest.of(page - 1, size));
        } else {
            try {
                return publisherAccountRepository.findAllByStatus(AccountStatus.valueOf(status),
                        PageRequest.of(page - 1, size));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid account status: " + status);
            }
        }
    }

    @Override
    public List<PublisherRole> getCustomRoleList() {
        return List.of();
    }

}

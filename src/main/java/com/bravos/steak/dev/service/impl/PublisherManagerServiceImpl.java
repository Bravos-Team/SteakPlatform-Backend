package com.bravos.steak.dev.service.impl;

import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.response.PublisherAccountListItem;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.service.PublisherManagerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublisherManagerServiceImpl implements PublisherManagerService {

    private final PublisherAccountRepository publisherAccountRepository;

    public PublisherManagerServiceImpl(PublisherAccountRepository publisherAccountRepository) {
        this.publisherAccountRepository = publisherAccountRepository;
    }

    @Override
    public Page<PublisherAccountListItem> getPublisherAccounts(int page, int size, String status) {
        var rawData = publisherAccountRepository.getListItem(PageRequest.of(page - 1,size), status);
        List<PublisherAccountListItem> items = new ArrayList<>(rawData.getSize());
        rawData.getContent().forEach(item -> {
            PublisherAccountListItem listItem = PublisherAccountListItem.builder()
                    .id(item.getId())
                    .username(item.getUsername())
                    .email(item.getEmail())
                    .roles(item.getRoles().stream().map(PublisherRole::getName).toList())
                    .build();
            items.add(listItem);
        });
        return new PageImpl<>(items,
                PageRequest.of(rawData.getNumber(), rawData.getSize()), rawData.getTotalElements());
    }

    @Override
    public List<PublisherRole> getCustomRoleList() {
        return List.of();
    }

}

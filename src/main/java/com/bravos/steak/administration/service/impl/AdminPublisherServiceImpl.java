package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.model.response.PublisherListItem;
import com.bravos.steak.administration.service.AdminPublisherService;
import com.bravos.steak.dev.model.enums.PublisherStatus;
import com.bravos.steak.dev.repo.PublisherRepository;
import com.bravos.steak.exceptions.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminPublisherServiceImpl implements AdminPublisherService {

    private final PublisherRepository publisherRepository;

    public AdminPublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public Page<PublisherListItem> getAllPublishers(int page, int size) {
        return publisherRepository.getAllPublishers(PageRequest.of(page, size));
    }

    @Override
    public Page<PublisherListItem> searchPublishers(String query, int page, int size) {
        return publisherRepository.getAllPublishersByName(query, PageRequest.of(page, size));
    }

    @Override
    public void updatePublisherStatus(Long publisherId, String status) {
        PublisherStatus publisherStatus;
        try {
            publisherStatus = PublisherStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid publisher status: " + status);
        }

        int changed;
        try {
            changed = publisherRepository.updateStatusById(publisherStatus,publisherId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update publisher status for publisherId: " + publisherId + ", status: " + status, e);
        }
        if(changed != 0) {

        }
    }

}

package com.bravos.steak.administration.service;

import com.bravos.steak.administration.model.response.PublisherListItem;
import org.springframework.data.domain.Page;

public interface AdminPublisherService {

    Page<PublisherListItem> getAllPublishers(int page, int size);

    Page<PublisherListItem> searchPublishers(String query, int page, int size);

    void updatePublisherStatus(Long publisherId, String status);

}

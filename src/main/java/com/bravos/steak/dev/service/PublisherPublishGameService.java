package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.SaveProjectRequest;
import org.springframework.stereotype.Service;

@Service
public interface PublisherPublishGameService {

    Long createProject(String projectName);

    void saveDraftProject(SaveProjectRequest saveProjectRequest);

}

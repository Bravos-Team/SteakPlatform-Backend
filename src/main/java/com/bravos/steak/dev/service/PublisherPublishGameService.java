package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.GameSubmissionListDisplay;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PublisherPublishGameService {

    Long createProject(String projectName);

    void saveDraftProject(SaveProjectRequest saveProjectRequest);

    void updateBuild(UpdatePreBuildRequest updatePreBuildRequest);

    void publishGame(Long projectId);

    List<GameSubmissionListDisplay> getProjectListByPublisher(
            String status,
            String keyword,
            int page,
            int size
    );


}

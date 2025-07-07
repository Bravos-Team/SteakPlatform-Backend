package com.bravos.steak.dev.service;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import com.bravos.steak.dev.model.request.PublisherReviewReplyRequest;
import com.bravos.steak.dev.model.request.SaveProjectRequest;
import com.bravos.steak.dev.model.request.UpdatePreBuildRequest;
import org.springframework.data.domain.Page;


public interface GameSubmissionService {

    Long createProject(String projectName);

    void saveDraftProject(SaveProjectRequest saveProjectRequest);

    void updateBuild(UpdatePreBuildRequest updatePreBuildRequest);

    void submitGameSubmission(Long projectId);

    Page<GameSubmissionListItem> getProjectListByPublisher(
            String status,
            String keyword,
            int page,
            int size
    );

    void reSubmitGameSubmission(PublisherReviewReplyRequest publisherReviewReplyRequest);


    void updateGameSubmissionStatus(Long submissionId, GameSubmissionStatus status);


}

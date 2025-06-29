package com.bravos.steak.dev.repo.custom;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListDisplay;

import java.util.List;

public interface CustomGameSubmissionRepository {

    Long getPublisherIdByProjectId(Long projectId);

    List<GameSubmissionListDisplay> getGameSubmissionListDisplay(
            Long publisherId,
            GameSubmissionStatus status,
            String keyword,
            int page,
            int size
    );

    GameSubmissionListDisplay getGameSubmissionListById(Long publisherId, Long id);

}

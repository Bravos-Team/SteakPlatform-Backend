package com.bravos.steak.dev.repo.custom;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListDisplay;
import org.springframework.data.domain.Page;

public interface CustomGameSubmissionRepository {

    Long getPublisherIdByProjectId(Long projectId);

    Page<GameSubmissionListDisplay> getGameSubmissionListDisplay(
            Long publisherId,
            GameSubmissionStatus status,
            String keyword,
            int page,
            int size
    );

    GameSubmissionListDisplay getGameSubmissionListById(Long publisherId, Long id);

}

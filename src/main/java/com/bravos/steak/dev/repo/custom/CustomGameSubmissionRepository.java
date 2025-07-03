package com.bravos.steak.dev.repo.custom;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface CustomGameSubmissionRepository {

    Long getPublisherIdByProjectId(Long projectId);

    Page<GameSubmissionListItem> getGameSubmissionListDisplay(
            Long publisherId,
            GameSubmissionStatus status,
            String keyword,
            int page,
            int size,
            Sort sort
    );

    GameSubmissionListItem getGameSubmissionListById(Long publisherId, Long id);

    PublisherIdAndStatus getPublisherIdAndStatusById(Long id);

    class PublisherIdAndStatus {

        public Long publisherId;

        public GameSubmissionStatus status;

    }

}

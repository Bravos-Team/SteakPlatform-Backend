package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.model.GameSubmissionListItem;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface GameReviewService {

    Page<GameSubmissionListItem> getNeedReviewGames(GameSubmissionStatus status, String keyword,
                                                    int page, int size, Sort sort);

    GameSubmission getGameSubmissionById(Long submissionId);

    void approveGameSubmission(Long submissionId);

    void responseToGameSubmission(Long submissionId, String response);

    void rejectGameSubmission(Long submissionId, String reason);

}

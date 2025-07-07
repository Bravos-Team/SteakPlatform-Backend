package com.bravos.steak.administration.service;

import com.bravos.steak.administration.entity.review.ReviewReply;
import com.bravos.steak.administration.model.request.ReviewerReviewReplyRequest;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmission;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import com.bravos.steak.dev.model.GameSubmissionListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface GameReviewService {

    Page<GameSubmissionListItem> getNeedReviewGames(GameSubmissionStatus status, String keyword,
                                                    Long publisherId, int page, int size, Sort sort);

    GameSubmission getGameSubmissionById(Long submissionId);

    void approveGameSubmission(Long submissionId);

    void requireUpdateGameSubmission(Long submissionId);

    void rejectGameSubmission(Long submissionId);

    List<ReviewReply> getReviewRepliesBySubmissionId(Long submissionId);

    ReviewReply createReviewReply(ReviewerReviewReplyRequest request);

}

package com.bravos.steak.administration.model.request;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewerReviewReplyRequest {

    @NotNull
    Long submissionId;

    @NotNull
    GameSubmissionStatus status;

    String content;

    String[] attachments;

}

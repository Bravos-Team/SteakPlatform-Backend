package com.bravos.steak.administration.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewReplyRequest {

    Long submissionId;

    String content;

    String[] attachments;

}

package com.bravos.steak.dev.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherReviewReplyRequest {

    Long submissionId;

    String content;

    String[] attachments;

}

package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherReviewReplyRequest {

    @NotNull
    Long submissionId;

    String content;

    String[] attachments;

}

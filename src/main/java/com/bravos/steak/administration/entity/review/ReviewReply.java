package com.bravos.steak.administration.entity.review;

import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("ReviewReply")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReviewReply {

    @Id
    @NonNull
    private Long id;

    @Field("gameSubmissionId")
    @NonNull
    @Indexed
    private Long gameSubmissionId;

    @Field("from")
    @NonNull
    private From from;

    @Field("content")
    @NonNull
    private String content;

    @Field("attachments")
    private String[] attachments;

    @Field("repliedAt")
    @NonNull
    private LocalDateTime repliedAt;

}

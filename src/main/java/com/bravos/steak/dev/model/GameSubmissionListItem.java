package com.bravos.steak.dev.model;

import com.bravos.steak.dev.entity.gamesubmission.BuildInfo;
import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * DTO for {@link com.bravos.steak.dev.entity.gamesubmission.GameSubmission}
 */
@Value
public class GameSubmissionListItem implements Serializable {
    Long id;
    Long publisherId;
    String name;
    String thumbnail;
    BuildInfo buildInfo;
    GameSubmissionStatus status;
    Long updatedAt;
}
package com.bravos.steak.dev.model;

import com.bravos.steak.dev.entity.gamesubmission.GameSubmissionStatus;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.bravos.steak.dev.entity.gamesubmission.GameSubmission}
 */
@Value
public class GameSubmissionListItem implements Serializable {
    Long id;
    Long publisherId;
    String name;
    GameSubmissionStatus status;
    String versionName;
    Date updatedAt;
}
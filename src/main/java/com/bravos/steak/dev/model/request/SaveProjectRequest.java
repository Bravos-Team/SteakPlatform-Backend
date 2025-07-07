package com.bravos.steak.dev.model.request;

import com.bravos.steak.store.entity.details.Media;
import com.bravos.steak.store.entity.details.SystemRequirements;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.bravos.steak.dev.entity.gamesubmission.GameSubmission}
 */
@Value
public class SaveProjectRequest implements Serializable {

    @NotNull(message = "Project ID cannot be null")
    Long id;

    String name;

    Double price;
    String[] developerTeam;
    String region;
    String thumbnail;
    Media[] media;
    String shortDescription;
    String longDescription;
    String[] platform;
    SystemRequirements systemRequirements;
    Boolean internetConnection;
    String[] languageSupported;
    Long estimatedReleaseDate;

}
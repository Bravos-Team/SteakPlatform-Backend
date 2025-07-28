package com.bravos.steak.dev.model.request;

import com.bravos.steak.store.entity.details.Media;
import com.bravos.steak.store.entity.details.SystemRequirements;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGameDetailsRequest {

    @NotNull
    Long gameId;

    private String title;

    private String[] developerTeams;

    private String[] regions;

    private String thumbnail;

    private Media[] media;

    private String shortDescription;

    private String longDescription;

    private String[] platforms;

    private SystemRequirements systemRequirements;

    private String[] languageSupported;

    private Set<Integer> genres;

    private Set<Integer> tags;

}

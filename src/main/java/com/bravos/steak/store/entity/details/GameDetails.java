package com.bravos.steak.store.entity.details;

import com.mongodb.lang.NonNull;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "GameDetails")
public class GameDetails {

    @Id
    @NonNull
    private Long id;

    @NonNull
    private String title;

    private String[] developersTeams;

    private String[] regions;

    private String thumbnail;

    private Media[] media;

    private String shortDescription;

    private String longDescription;

    private String[] platforms;

    private SystemRequirements systemRequirements;

    private String[] languageSupported;

    @NonNull
    private Long updatedAt;

}

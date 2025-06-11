package com.bravos.steak.dev.entity.gamesubmission;

import com.bravos.steak.store.entity.details.Media;
import com.bravos.steak.store.entity.details.SystemRequirements;
import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@CompoundIndex(name = "idx_name_publisher", def = "{'name' : 1, 'publisherId': 1}", unique = true)
public class GameSubmission {

    @Id
    @NonNull
    private long id;

    @Field("publisherId")
    @NonNull
    @Indexed
    private long publisherId;

    @Field("createdBy")
    @NonNull
    private long createdBy;

    @Field("name")
    @NonNull
    private String name;

    @Field("gameTitle")
    private String gameTitle;

    @Field("price")
    private double price;

    @Field("developerTeam")
    private List<String> developerTeam;

    @Field("region")
    private String region;

    @Field("thumbnail")
    private String thumbnail;

    @Field("media")
    private List<Media> media;

    @Field("shortDescription")
    private String shortDescription;

    @Field("longDescription")
    private String longDescription;

    @Field("platform")
    private String platform;

    @Field("systemRequirements")
    private SystemRequirements systemRequirements;

    @Field("internetConnection")
    private String internetConnection;

    @Field("languageSupported")
    private List<String> languageSupported;

    @Field("buildInfo")
    private BuildInfo buildInfo;

    @Field("status")
    private String status;

    @Field("updatedAt")
    private String updatedAt;

    public GameSubmission(long id, long publisherId, long createdBy, @NotNull String name) {
        this.id = id;
        this.publisherId = publisherId;
        this.createdBy = createdBy;
        this.name = name;
    }

}

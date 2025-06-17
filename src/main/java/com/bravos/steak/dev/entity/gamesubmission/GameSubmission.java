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

import java.util.Date;

@Document("GameSubmission")
@Getter
@Setter
@NoArgsConstructor
@CompoundIndex(name = "idx_name_publisher", def = "{'name' : 1, 'publisherId': 1}", unique = true)
public class GameSubmission {

    @Id
    @NonNull
    private Long id;

    @Field("publisherId")
    @NonNull
    @Indexed
    private Long publisherId;

    @Field("createdBy")
    @NonNull
    private Long createdBy;

    @Field("name")
    @NonNull
    private String name;

    @Field("price")
    private Double price;

    @Field("developerTeam")
    private String[] developerTeam;

    @Field("region")
    private String region;

    @Field("thumbnail")
    private String thumbnail;

    @Field("media")
    private Media[] media;

    @Field("shortDescription")
    private String shortDescription;

    @Field("longDescription")
    private String longDescription;

    @Field("platform")
    private String platform;

    @Field("systemRequirements")
    private SystemRequirements systemRequirements;

    @Field("internetConnection")
    private Boolean internetConnection;

    @Field("languageSupported")
    private String[] languageSupported;

    @Field("estimatedReleaseDate")
    private Date estimatedReleaseDate;

    @Field("buildInfo")
    private BuildInfo buildInfo;

    @Field("status")
    private GameSubmissionStatus status = GameSubmissionStatus.DRAFT;

    @Field("updatedAt")
    private Date updatedAt;

    public GameSubmission(long id, long publisherId, long createdBy, @NotNull String name) {
        this.id = id;
        this.publisherId = publisherId;
        this.createdBy = createdBy;
        this.name = name;
    }

}

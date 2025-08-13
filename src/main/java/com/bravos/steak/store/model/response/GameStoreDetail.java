package com.bravos.steak.store.model.response;

import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.entity.details.GameDetails;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameStoreDetail {

    private GameDetails details;

    private Double price;

    private String publisherName;

    private List<Genre> genres;

    private List<Tag> tags;

    private Boolean isOwned;

    private String latestVersionName;

}

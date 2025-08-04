package com.bravos.steak.dev.model.response;

import com.bravos.steak.store.entity.Genre;
import com.bravos.steak.store.entity.Tag;
import com.bravos.steak.store.model.enums.GameStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class GameSQLInfo {

    Long id;

    String name;

    BigDecimal price;

    GameStatus status;

    Long releaseDate;

    Set<Genre> genres;

    Set<Tag> tags;

    Long createdAt;

    Long updatedAt;

}

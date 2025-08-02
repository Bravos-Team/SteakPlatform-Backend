package com.bravos.steak.administration.model.response;

import com.bravos.steak.store.model.enums.GameStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameListItem {

    Long id;

    String title;

    Long publisherId;

    String publisherName;

    Long releaseDate;

    GameStatus status;

}

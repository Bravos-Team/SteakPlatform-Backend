package com.bravos.steak.dev.model.response;

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
public class PublisherGameListItem {

    Long gameId;

    String title;

    GameStatus status;

    String thumbnail;

}

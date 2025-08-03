package com.bravos.steak.store.model.response;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.details.GameDetails;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class FullGameDetails {

    Game game;

    GameDetails gameDetails;

}

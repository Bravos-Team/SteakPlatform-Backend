package com.bravos.steak.store.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameListItem {

    Long id;
    String name;
    String thumbnail;
    BigDecimal price;
    Long createAt;
    Long updatedAt;
    Long releaseDate;
}

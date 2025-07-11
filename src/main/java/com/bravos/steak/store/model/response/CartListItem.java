package com.bravos.steak.store.model.response;

import com.bravos.steak.store.repo.injection.CartGameInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CartListItem {

    Long id;

    String title;

    String thumbnail;

    Double price;

    public CartListItem(CartGameInfo cartGameInfo) {
        this.id = cartGameInfo.getId();
        this.title = cartGameInfo.getTitle();
        this.thumbnail = cartGameInfo.getThumbnail();
    }

}

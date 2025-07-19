package com.bravos.steak.store.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameListResponse {

    List<GameListItem> items;

    Long nextCursor;

}

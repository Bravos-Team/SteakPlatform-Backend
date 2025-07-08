package com.bravos.steak.store.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameLibraryItem {

    private Long id;

    private Long gameId;

    private String title;

    private String thumbnailUrl;

}

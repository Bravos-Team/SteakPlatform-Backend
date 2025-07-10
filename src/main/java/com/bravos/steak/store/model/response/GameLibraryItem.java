package com.bravos.steak.store.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameLibraryItem {

    private Long libraryId;

    private Long gameId;

    private String title;

    private String thumbnailUrl;

}

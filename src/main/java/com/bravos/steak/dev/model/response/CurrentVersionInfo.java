package com.bravos.steak.dev.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrentVersionInfo implements Serializable {

    Long gameId;

    String title;

    GameVersionListItem currentVersion;

    GameVersionListItem nextVersion;

}

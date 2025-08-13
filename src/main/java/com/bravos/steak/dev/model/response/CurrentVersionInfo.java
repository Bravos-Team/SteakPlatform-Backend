package com.bravos.steak.dev.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrentVersionInfo {

    Long gameId;

    String title;

    String currentVersion;

    String nextVersion;

}

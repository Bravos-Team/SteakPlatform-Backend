package com.bravos.steak.dev.model.response;

import com.bravos.steak.store.model.enums.VersionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GameVersionListItem {

    Long versionId;

    String name;

    String changeLog;

    String execPath;

    String downloadUrl;

    VersionStatus status;

    Long releaseDate;

    Long fileSize;

    Long installSize;

    String checksum;

    Long createdAt;

    Long updatedAt;

}

package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.store.model.enums.VersionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "game_version")
public class GameVersion {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    Game game;

    String name;

    String changeLog;

    String execPath;

    String downloadUrl;

    @Enumerated(EnumType.ORDINAL)
    VersionStatus status;

    Long releaseDate;

    @Builder.Default
    Long createdAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    Long updatedAt = DateTimeHelper.currentTimeMillis();

}

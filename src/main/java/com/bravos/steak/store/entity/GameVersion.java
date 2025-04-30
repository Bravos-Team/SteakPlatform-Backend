package com.bravos.steak.store.entity;

import com.bravos.steak.store.model.enums.VersionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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

    LocalDateTime releaseDate;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    LocalDateTime updatedAt = LocalDateTime.now();

}

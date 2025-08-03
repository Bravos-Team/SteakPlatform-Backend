package com.bravos.steak.store.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.store.model.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    String name;

    @Column(precision = 13, scale = 2, nullable = false)
    BigDecimal price;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    GameStatus status = GameStatus.CLOSED;

    Long releaseDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_tag",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    Set<Tag> tags;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<GameVersion> gameVersions = new ArrayList<>();

    @Builder.Default
    Long createdAt = DateTimeHelper.currentTimeMillis();

    @Builder.Default
    Long updatedAt = DateTimeHelper.currentTimeMillis();

}

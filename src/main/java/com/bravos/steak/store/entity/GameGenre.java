package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "game_genre",
        uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "genre_id"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameGenre {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    Game game;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    Genre genre;

}

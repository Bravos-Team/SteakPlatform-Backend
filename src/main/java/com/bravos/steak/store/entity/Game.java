package com.bravos.steak.store.entity;

import com.bravos.steak.store.model.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Game {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "age", nullable = false)
    int age;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    GameStatus status = GameStatus.OPENING;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    Publisher publisher;

    @Builder.Default
    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime created_at = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updated_at = LocalDateTime.now();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<GameGenre> gameGenres;

}

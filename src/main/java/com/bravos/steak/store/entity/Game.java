package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Game {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    @Column(name = "price", nullable = false)
    Long price;

    @Column(name = "age", nullable = false)
    int age;

    /*
    * Slider + Image cover
    * */
    @Column(name = "status", nullable = false)
    int status = 0;

    @NotNull
    @Column(name = "showcase_id", nullable = false)
    String showcaseId;

    @Column(name = "created_at")
    @Builder.Default
    Instant created_at = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    Instant updated_at = Instant.now();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<Game_Genre> gameGenres;
}

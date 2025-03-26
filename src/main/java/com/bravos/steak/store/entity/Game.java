package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "name", nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    @Column(name = "price", nullable = false)
    Long price;

    @Column(name = "age", nullable = false)
    int age;

    @Column(name = "status", nullable = false)
    int status = 0;

    @Column(name = "showcase_id", nullable = false)
    String showcaseId;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updated_at;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    List<Game_Genre> gameGenres;
}

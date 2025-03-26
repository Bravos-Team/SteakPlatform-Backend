package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "genre")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "slug", nullable = false, unique = true)
    String slug;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    List<Game_Genre> gameGenres;

}

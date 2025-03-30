package com.bravos.steak.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;

import java.util.List;

@Entity
@Table(name = "publisher")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Publisher {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    String email;

    @NotNull
    @Column(name = "phone", nullable = false)
    String phone;

    @NotNull
    @Column(name = "avatar", nullable = false)
    String avatar;

    @NotNull
    @Column(name = "description", nullable = false)
    String text;

    @OneToMany(mappedBy = "publisher")
    List<Game> games;

}

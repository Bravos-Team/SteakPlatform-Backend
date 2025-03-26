package com.bravos.steak.store.entity;

import jakarta.persistence.*;
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

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "phone", nullable = false)
    String phone;

    @Column(name = "avatar", nullable = false)
    String avatar;

    @Column(name = "description", nullable = false)
    String text;

    @OneToMany(mappedBy = "publisher")
    List<Game> games;

}

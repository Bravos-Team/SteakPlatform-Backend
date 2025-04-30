package com.bravos.steak.dev.entity;

import com.bravos.steak.dev.model.enums.PublisherStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "publisher")
public class Publisher {

    @Id
    Long id;

    String name;

    String email;

    String phone;

    @Enumerated(EnumType.ORDINAL)
    PublisherStatus status;

    String logoUrl;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
